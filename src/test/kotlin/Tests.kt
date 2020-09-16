class Tests {
}

fun main() {
    val list = SkipList<Int>()
    list.print()
    list.add(1)
    list.add(2)
    list.add(3)
    list.add(4)
    list.add(5)
    list.add(6)
    println("====Start print====")
    list.print()
    println("=====End print=====")
    println("our test is delete 4 and see deleted nodes at top layers too")
    list.remove(4)
    println("====Start print====")
    list.print()
    println("=====End print=====")
    list.remove(1)
    list.remove(6)
    println("====Start print====")
    list.print()
    println("=====End print=====")
}