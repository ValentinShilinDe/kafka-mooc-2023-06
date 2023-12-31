object scalaintro extends App{
    //1. everything object

    val a = 1+1
    val b = println("Hi") //unit

    //скала это функциональный язык, каждая функция это згначение
    val inc: Int => Int = x => x+1
    inc(1)
    val c = inc
    // Скала это ООП язык, т.к. каждое значение в скале это обьект
    // Скала это ФП язык, т.к. каждая функция в скала это значение
    //скала это гибридный язык и каждая функция это обьект
    //Scala !- Haskel
    //Scala != Better Java



    /**
     * Управляющие конструкции
     *   if / else
     *   while / do while
     *   for
     */



    /**
     *  Конструкция if / else имеет туже семантику, что и в других ЯП. В зависимости от условия, выполняется либо одна либо
     *  другая ветка.
     *  При этом тип и значение if / else выражения определяется также, как и для блока кода.
     *  Т.е. последним выражением в исполняемой ветке.
     *
     */

    lazy val cond: Boolean = true


    //4. Напишите выражение, которое в зависимости от значения выражения cond
    // будет возвращать "yes" или "no",
    // присвойте его в переменную х1

    val x1: String = if (cond) "yes" else "no"



    //5. Напишите выражение, но которое в зависимости от значения выражения cond
    // будет печатать "yes" или "no" в консоль,
    // присвойте его в переменную х2

    val x2 = if (cond) println("yes") else println("no")

    //6. Напишите выражение, которое если значение переменной cond будет true напечатает в консоль "yes", а если
    // false то вернет строку "no",
    // присвойте его в переменную х3

    val x3: Any = if (cond) println("yes") else "no"



    /**
     * циклы while / do while
     * Повторяют выполнение своего тела, пока условие истинно. Подразумевают наличие side effect.
     * Отличаются моментом, когда происходит проверка условия ДО или ПОСЛЕ выполнения тела цикла
     */


    val x: Unit = do{
        println("hey")

    } while (cond)

    /**
     * цикл for позволяет итерироваться по коллекциям,
     * имеет своеобразный синтаксис с обратной стрелочкой
     */

    val l = List(1, 2, 3)
    for(i <- 0 until  10 by 2){
        println(i)
    }




}