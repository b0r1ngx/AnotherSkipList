import kotlin.random.Random

/**
 * SkipList consists of layers
 *
 * Each layer is a Double Linked List, values are stored in a sorted order
 * DLL contains dummy head and tail nodes, they store no value and are used only for iteration
 * [head] -> [data-node-1] -> [data-node-2] <- [tail]
 *
 * Each node refers to prev/next nodes of same layer and up/down nodes to move between layers
 * If node reference (next/prev/up/down) points to itself, consider it being null
 *
 * Maximum height of one layer can be, is log(SkipList.size) (theoretical)
 */
class SkipList<E : Comparable<E>> {

    private class Node<E>(val value: E?) {
        // point to 'this' to avoid nullable data types
        var next: Node<E> = this
        var prev: Node<E> = this
        var down: Node<E> = this
        var up: Node<E> = this

        // is it real node or dummy (head/tail)
        val isDataNode: Boolean get() = value != null
        val isFakeNode: Boolean get() = value == null

        val hasDown: Boolean get() = down != this
        val hasUp: Boolean get() = up != this
        val hasNext: Boolean get() = next != this
        val hasPrev: Boolean get() = prev != this

        tailrec fun btm(n: Node<E>): Node<E> = if (n.down == n) n else btm(n.down)
        tailrec fun top(n: Node<E>): Node<E> = if (n.up == n) n else top(n.up)

        val bottom: Node<E> by lazy(LazyThreadSafetyMode.NONE) { btm(down) } // references bottom node
        val top: Node<E> get() = top(up) // references top node

        override fun toString(): String {
            return when {
                value != null -> value.toString()
                prev == this -> "head"
                next == this -> "tail"
                else -> "undef"
            }
        }
    }

    companion object {
        const val MIN_SIZE_FOR_NEW_LAYER = 3
    }

    private val head = Node<E>(null) // of the bottom layer
    private val tail = Node<E>(null) // of the bottom layer
    private var size = 0

    init {
        head.next = tail
        tail.prev = head
    }

    private val rnd = Random.Default

    /**
     * Adds the specified element to the end of this list.
     *
     * @return `true` because the list is always modified as the result of this operation.
     */
    fun add(element: E): Boolean {
        val nextNode = search(element)

        val newNode = Node(element)

        newNode.prev = nextNode.prev
        newNode.next = nextNode

        nextNode.prev.next = newNode
        nextNode.prev = newNode

        size++

        lift(newNode)
        return true
    }

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present.
     *
     * @return `true` if the element has been successfully removed;
     * `false` if it was not present in the collection.
     *
     */
//  not delete nodes on top layers...
    fun remove(element: E): Boolean {
        var r = search(element)
        if (r.value != element) return false
        // if removing node is in middle
        if (r.prev.hasPrev && r.next.hasNext)
            r.prev.next = r.next.also { r.next.prev = r.prev }
        size --
        return true
    }

    /**
     * Checks if the specified element is contained in this collection.
     */
    fun contains(element: E): Boolean {
        return element == search(element).value
    }

    // h ->  10 ---------------- 90 <- t
    // h ->  10 - 30 - 50 - 70 - 90 <- t
    // returns either node equals to target or next node that is greater than target
    private fun search(target: E): Node<E> {
        var curr = head.top

        if (curr.next.isFakeNode) // empty layer
            return tail

        while (true) {
            while ((curr.isFakeNode || curr.value!! < target) && curr.next.isDataNode && curr.next.value!! < target)
                curr = curr.next

            curr = when {
                curr.isFakeNode && curr.hasDown -> curr.down
                curr.isDataNode && curr.value!! >= target -> return curr.bottom // found value, return bottom node
                curr.hasDown -> curr.down // try search on 1 layer below
                else -> return curr.next // return next node (either greater or tail)
            }
        }
    }

    /**
     * Prints our SkipList
     *
     * My thoughts this realisation is quite look more quick to informatively about links of top layers,
     * instead of this prints example:
     *
     * h ->  10 ------------55----------- 90 <- t
     * h ->  10 - 30 - 50 - 55 - 60 -70 - 90 <- t
     */
    fun print() {
        var node = head.top

        println(toString(node))
        while (node.hasDown) {
            node = node.down
            println(toString(node))
        }
    }

    private fun toString(start: Node<E>): String {
        var node = start
        val sb = StringBuilder()
        while (node.hasNext) {
            sb.append(node).append(" -> ")
            node = node.next
        }
        sb.append(node)

        return sb.toString()
    }

    /**
     * Randomly push through new created (added) Node to the top layers of specified height
     */
    private fun lift(newNode: Node<E>) { // copy to the top layers
        var currLayer = head
        var insert = newNode

        val alwaysLift = false

        while (currLayer.hasUp && (alwaysLift || rnd.nextBoolean())) { // adding to existing layers
            insert = createNodeOnTopOf(insert)
            currLayer = currLayer.up
        }

        if (!currLayer.hasUp && needAddNewLayer()) { // maybe create new layer
            val currTopHead = head.top
            val newTopHead = Node<E>(null)
            newTopHead.down = currTopHead
            currTopHead.up = newTopHead

            val currTopTail = tail.top
            val newTopTail = Node<E>(null)
            newTopTail.down = currTopTail
            currTopTail.up = newTopTail

            createNodeOnTopOf(insert)
        }
    }

    private fun needAddNewLayer(): Boolean = size > MIN_SIZE_FOR_NEW_LAYER && rnd.nextBoolean()

    private fun createNodeOnTopOf(newNode: Node<E>): Node<E> {
        val upNode = Node(newNode.value)
        upNode.down = newNode
        newNode.up = upNode

        var left = newNode.prev
        while (!left.hasUp && left.isDataNode) left = left.prev

        if (!left.hasUp) throw IllegalStateException("$left")
        left.up.next = upNode
        upNode.prev = left.up

        var right = newNode.next
        while (!right.hasUp && right.isDataNode) right = right.next
        if (!right.hasUp) throw IllegalStateException("$right")
        right.up.prev = upNode
        upNode.next = right.up

        return upNode
    }
}