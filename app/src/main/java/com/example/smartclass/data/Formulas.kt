package com.example.smartclass.data

data class Formula(
    val id: Int,
    val name: String,
    val formula: String,
    val description: String,
    val category: String,
    val grade: Int
)

object Formulas {
    val formulas = listOf(
        // Grade 7 Formulas
        Formula(
            id = 1,
            name = "Линейное уравнение",
            formula = "ax + b = c → x = (c - b) / a",
            description = "Стандартная форма линейного уравнения и его решение",
            category = "Уравнения",
            grade = 7
        ),
        Formula(
            id = 2,
            name = "Распределительное свойство",
            formula = "a(b + c) = ab + ac",
            description = "Умножьте множитель на каждый член в скобках",
            category = "Свойства",
            grade = 7
        ),
        Formula(
            id = 3,
            name = "Сложение подобных членов",
            formula = "ax + bx = (a + b)x",
            description = "Сложите коэффициенты членов с одинаковыми переменными",
            category = "Выражения",
            grade = 7
        ),
        // Новые формулы для 7 класса
        Formula(
            id = 19,
            name = "Процент от числа",
            formula = "P% от A = (P/100) × A",
            description = "Вычисление процента от числа",
            category = "Проценты",
            grade = 7
        ),
        Formula(
            id = 20,
            name = "Процентное изменение",
            formula = "Δ% = [(Новое - Старое)/Старое] × 100%",
            description = "Вычисление процентного изменения",
            category = "Проценты",
            grade = 7
        ),
        Formula(
            id = 21,
            name = "Расстояние между точками",
            formula = "d = √[(x₂ - x₁)² + (y₂ - y₁)²]",
            description = "Расстояние между двумя точками на координатной плоскости",
            category = "Геометрия",
            grade = 7
        ),
        Formula(
            id = 22,
            name = "Середина отрезка",
            formula = "M = ((x₁+x₂)/2, (y₁+y₂)/2)",
            description = "Координаты середины отрезка",
            category = "Геометрия",
            grade = 7
        ),
        Formula(
            id = 23,
            name = "Увеличение на процент",
            formula = "Новое = Старое × (1 + p/100)",
            description = "Вычисление нового значения после увеличения на p%",
            category = "Проценты",
            grade = 7
        ),
        Formula(
            id = 24,
            name = "Уменьшение на процент",
            formula = "Новое = Старое × (1 - p/100)",
            description = "Вычисление нового значения после уменьшения на p%",
            category = "Проценты",
            grade = 7
        ),

        // Grade 8 Formulas
        Formula(
            id = 4,
            name = "Квадратная формула",
            formula = "x = (-b ± √(b² - 4ac)) / 2a",
            description = "Решает ax² + bx + c = 0",
            category = "Уравнения",
            grade = 8
        ),
        Formula(
            id = 5,
            name = "Дискриминант",
            formula = "D = b² - 4ac",
            description = "Определяет количество решений: D>0 (2), D=0 (1), D<0 (0)",
            category = "Уравнения",
            grade = 8
        ),
        Formula(
            id = 6,
            name = "Разность квадратов",
            formula = "a² - b² = (a + b)(a - b)",
            description = "Шаблон разложения для разности двух квадратов",
            category = "Разложение",
            grade = 8
        ),
        Formula(
            id = 7,
            name = "Полный квадрат трехчлена",
            formula = "a² + 2ab + b² = (a + b)²",
            description = "Шаблон разложения для полного квадрата",
            category = "Разложение",
            grade = 8
        ),
        Formula(
            id = 8,
            name = "Полный квадрат трехчлена (отрицательный)",
            formula = "a² - 2ab + b² = (a - b)²",
            description = "Шаблон разложения для полного квадрата с вычитанием",
            category = "Разложение",
            grade = 8
        ),
        // Новые формулы для 8 класса
        Formula(
            id = 25,
            name = "Теорема Пифагора",
            formula = "a² + b² = c²",
            description = "В прямоугольном треугольнике квадрат гипотенузы равен сумме квадратов катетов",
            category = "Геометрия",
            grade = 8
        ),
        Formula(
            id = 26,
            name = "Обратная теорема Пифагора",
            formula = "Если a² + b² = c², то треугольник прямоугольный",
            description = "Проверка прямоугольности треугольника",
            category = "Геометрия",
            grade = 8
        ),
        Formula(
            id = 27,
            name = "Сумма квадратов в разложении",
            formula = "x⁴ + 4y⁴ = (x² + 2y²)² - 4x²y²",
            description = "Полезное тождество для разложения на множители",
            category = "Разложение",
            grade = 8
        ),
        Formula(
            id = 28,
            name = "Рациональное выражение (произведение)",
            formula = "(a/b) × (c/d) = (a×c)/(b×d)",
            description = "Умножение рациональных выражений",
            category = "Дроби",
            grade = 8
        ),
        Formula(
            id = 29,
            name = "Рациональное выражение (деление)",
            formula = "(a/b) ÷ (c/d) = (a×d)/(b×c)",
            description = "Деление рациональных выражений",
            category = "Дроби",
            grade = 8
        ),

        // Grade 9 Formulas
        Formula(
            id = 9,
            name = "Формула наклона",
            formula = "m = (y₂ - y₁) / (x₂ - x₁)",
            description = "Вычислите наклон между двумя точками",
            category = "Функции",
            grade = 9
        ),
        Formula(
            id = 10,
            name = "Форма наклон-отсечка",
            formula = "y = mx + b",
            description = "m = наклон, b = y-отсечка",
            category = "Функции",
            grade = 9
        ),
        Formula(
            id = 11,
            name = "Точечно-наклонная форма",
            formula = "y - y₁ = m(x - x₁)",
            description = "Уравнение, использующее точку и наклон",
            category = "Функции",
            grade = 9
        ),
        Formula(
            id = 12,
            name = "Произведение степеней",
            formula = "aᵐ × aⁿ = aᵐ⁺ⁿ",
            description = "Умножьте одинаковые основания: сложите показатели",
            category = "Степени",
            grade = 9
        ),
        Formula(
            id = 13,
            name = "Частное степеней",
            formula = "aᵐ ÷ aⁿ = aᵐ⁻ⁿ",
            description = "Разделите одинаковые основания: вычтите показатели",
            category = "Степени",
            grade = 9
        ),
        Formula(
            id = 14,
            name = "Степень степени",
            formula = "(aᵐ)ⁿ = aᵐⁿ",
            description = "Возведите степень в степень: умножьте показатели",
            category = "Степени",
            grade = 9
        ),
        Formula(
            id = 15,
            name = "Нулевой показатель",
            formula = "a⁰ = 1 (a ≠ 0)",
            description = "Любое ненулевое число в степени 0 равно 1",
            category = "Степени",
            grade = 9
        ),
        Formula(
            id = 16,
            name = "Отрицательный показатель",
            formula = "a⁻ⁿ = 1/aⁿ",
            description = "Отрицательный показатель означает обратное",
            category = "Степени",
            grade = 9
        ),
        Formula(
            id = 17,
            name = "Свойство квадратного корня",
            formula = "√(ab) = √a × √b",
            description = "Квадратный корень произведения равен произведению квадратных корней",
            category = "Радикалы",
            grade = 9
        ),
        Formula(
            id = 18,
            name = "Формула расстояния",
            formula = "d = √((x₂-x₁)² + (y₂-y₁)²)",
            description = "Расстояние между двумя точками на координатной плоскости",
            category = "Функции",
            grade = 9
        ),
        // Новые формулы для 9 класса
        Formula(
            id = 30,
            name = "n-й член арифметической прогрессии",
            formula = "aₙ = a₁ + (n-1)d",
            description = "Нахождение любого члена арифметической прогрессии",
            category = "Последовательности",
            grade = 9
        ),
        Formula(
            id = 31,
            name = "Сумма арифметической прогрессии",
            formula = "Sₙ = n/2 × [2a₁ + (n-1)d]",
            description = "Сумма первых n членов арифметической прогрессии",
            category = "Последовательности",
            grade = 9
        ),
        Formula(
            id = 32,
            name = "Другая формула суммы АП",
            formula = "Sₙ = n/2 × (a₁ + aₙ)",
            description = "Альтернативная формула суммы через первый и последний члены",
            category = "Последовательности",
            grade = 9
        ),
        Formula(
            id = 33,
            name = "n-й член геометрической прогрессии",
            formula = "aₙ = a₁ × qⁿ⁻¹",
            description = "Нахождение любого члена геометрической прогрессии",
            category = "Последовательности",
            grade = 9
        ),
        Formula(
            id = 34,
            name = "Сумма геометрической прогрессии",
            formula = "Sₙ = a₁ × (1 - qⁿ)/(1 - q), q ≠ 1",
            description = "Сумма первых n членов геометрической прогрессии",
            category = "Последовательности",
            grade = 9
        ),
        Formula(
            id = 35,
            name = "Определение логарифма",
            formula = "logₐb = c ⇔ aᶜ = b",
            description = "Основное определение логарифма",
            category = "Логарифмы",
            grade = 9
        ),
        Formula(
            id = 36,
            name = "Логарифм произведения",
            formula = "logₐ(mn) = logₐm + logₐn",
            description = "Логарифм произведения равен сумме логарифмов",
            category = "Логарифмы",
            grade = 9
        ),
        Formula(
            id = 37,
            name = "Логарифм частного",
            formula = "logₐ(m/n) = logₐm - logₐn",
            description = "Логарифм частного равен разности логарифмов",
            category = "Логарифмы",
            grade = 9
        ),
        Formula(
            id = 38,
            name = "Логарифм степени",
            formula = "logₐ(mᵖ) = p × logₐm",
            description = "Логарифм степени: показатель выносится как множитель",
            category = "Логарифмы",
            grade = 9
        ),
        Formula(
            id = 39,
            name = "Формула перехода к новому основанию",
            formula = "logₐb = logₖb / logₖa",
            description = "Переход к логарифму с другим основанием",
            category = "Логарифмы",
            grade = 9
        ),
        Formula(
            id = 40,
            name = "Основное тригонометрическое тождество",
            formula = "sin²θ + cos²θ = 1",
            description = "Связь между синусом и косинусом одного угла",
            category = "Тригонометрия",
            grade = 9
        ),
        Formula(
            id = 41,
            name = "Тангенс через синус и косинус",
            formula = "tan θ = sin θ / cos θ",
            description = "Определение тангенса через синус и косинус",
            category = "Тригонометрия",
            grade = 9
        ),
        Formula(
            id = 42,
            name = "Факториал",
            formula = "n! = 1 × 2 × 3 × ... × n",
            description = "Произведение всех натуральных чисел от 1 до n",
            category = "Комбинаторика",
            grade = 9
        ),
        Formula(
            id = 43,
            name = "Количество перестановок",
            formula = "Pₙ = n!",
            description = "Количество способов упорядочить n различных объектов",
            category = "Комбинаторика",
            grade = 9
        ),
        Formula(
            id = 44,
            name = "Количество сочетаний",
            formula = "Cₙᵏ = n! / [k! × (n-k)!]",
            description = "Количество способов выбрать k объектов из n без учета порядка",
            category = "Комбинаторика",
            grade = 9
        ),
        Formula(
            id = 45,
            name = "Количество размещений",
            formula = "Aₙᵏ = n! / (n-k)!",
            description = "Количество способов выбрать k объектов из n с учетом порядка",
            category = "Комбинаторика",
            grade = 9
        ),
        Formula(
            id = 46,
            name = "Вероятность события",
            formula = "P(A) = m / n",
            description = "Вероятность равна отношению благоприятных исходов к общему числу исходов",
            category = "Вероятность",
            grade = 9
        ),
        Formula(
            id = 47,
            name = "Вероятность противоположного события",
            formula = "P(не A) = 1 - P(A)",
            description = "Вероятность того, что событие не произойдет",
            category = "Вероятность",
            grade = 9
        ),
        Formula(
            id = 48,
            name = "Модуль комплексного числа",
            formula = "|a + bi| = √(a² + b²)",
            description = "Расстояние от комплексного числа до начала координат",
            category = "Комплексные числа",
            grade = 9
        ),
        Formula(
            id = 49,
            name = "Сопряженное комплексное число",
            formula = "a + bi = a - bi",
            description = "Комплексно-сопряженное число (меняется знак мнимой части)",
            category = "Комплексные числа",
            grade = 9
        ),
        Formula(
            id = 50,
            name = "Формула корней квадратного уравнения (через D)",
            formula = "x₁,₂ = (-b ± √D) / 2a",
            description = "Запись квадратной формулы через дискриминант",
            category = "Уравнения",
            grade = 9
        ),
        Formula(
            id = 51,
            name = "Объединение множеств",
            formula = "A ∪ B = {x | x ∈ A или x ∈ B}",
            description = "Элементы, принадлежащие хотя бы одному из множеств",
            category = "Множества",
            grade = 9
        ),
        Formula(
            id = 52,
            name = "Пересечение множеств",
            formula = "A ∩ B = {x | x ∈ A и x ∈ B}",
            description = "Элементы, принадлежащие обоим множествам одновременно",
            category = "Множества",
            grade = 9
        ),
        Formula(
            id = 53,
            name = "Разность множеств",
            formula = "A \\ B = {x | x ∈ A и x ∉ B}",
            description = "Элементы, принадлежащие первому множеству, но не второму",
            category = "Множества",
            grade = 9
        )
    )

    fun getFormulasByGrade(grade: Int): List<Formula> =
        formulas.filter { it.grade == grade }

    fun getFormulasByCategory(category: String): List<Formula> =
        formulas.filter { it.category == category }

    fun getAllCategories(): List<String> =
        formulas.map { it.category }.distinct()
}