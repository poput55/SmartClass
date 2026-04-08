package com.example.smartclass.data

data class Question(
    val id: Int,
    val topicId: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val difficulty: Difficulty
)

enum class Difficulty {
    EASY, MEDIUM, HARD
}

object Questions {
    val questions = listOf(
        // Linear Equations (Topic 1)
        Question(
            id = 1,
            topicId = 1,
            question = "Решите: 3x + 7 = 22",
            options = listOf("x = 3", "x = 5", "x = 7", "x = 4"),
            correctAnswer = 1,
            explanation = "3x = 22 - 7 = 15, следовательно x = 15/3 = 5",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 2,
            topicId = 1,
            question = "Решите: 2x - 8 = 4x + 2",
            options = listOf("x = -5", "x = 5", "x = -3", "x = 3"),
            correctAnswer = 0,
            explanation = "2x - 4x = 2 + 8, -2x = 10, x = -5",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 3,
            topicId = 1,
            question = "Решите: 5(x - 2) = 3x + 4",
            options = listOf("x = 5", "x = 6", "x = 7", "x = 8"),
            correctAnswer = 2,
            explanation = "5x - 10 = 3x + 4, 2x = 14, x = 7",
            difficulty = Difficulty.MEDIUM
        ),

        // Algebraic Expressions (Topic 2)
        Question(
            id = 4,
            topicId = 2,
            question = "Упростите: 3x + 5x - 2x",
            options = listOf("6x", "8x", "10x", "4x"),
            correctAnswer = 0,
            explanation = "3x + 5x - 2x = (3 + 5 - 2)x = 6x",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 5,
            topicId = 2,
            question = "Каков коэффициент x в: 7x² - 4x + 9?",
            options = listOf("7", "-4", "9", "4"),
            correctAnswer = 1,
            explanation = "Коэффициент x - это число, умноженное на x, которое равно -4",
            difficulty = Difficulty.EASY
        ),

        // Polynomials (Topic 3)
        Question(
            id = 6,
            topicId = 3,
            question = "Какова степень: 4x³ + 2x² - x + 5?",
            options = listOf("1", "2", "3", "4"),
            correctAnswer = 2,
            explanation = "Наивысшая степень x равна 3, следовательно степень равна 3",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 7,
            topicId = 3,
            question = "Сложите: (2x² + 3x) + (x² - 5x + 2)",
            options = listOf("3x² - 2x + 2", "3x² + 8x + 2", "2x² - 2x + 2", "3x² - 2x - 2"),
            correctAnswer = 0,
            explanation = "(2x² + x²) + (3x - 5x) + 2 = 3x² - 2x + 2",
            difficulty = Difficulty.MEDIUM
        ),

        // Quadratic Equations (Topic 4)
        Question(
            id = 8,
            topicId = 4,
            question = "Решите: x² - 9 = 0",
            options = listOf("x = 3", "x = -3", "x = ±3", "x = 9"),
            correctAnswer = 2,
            explanation = "x² = 9, x = ±√9 = ±3",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 9,
            topicId = 4,
            question = "Решите: x² - 7x + 12 = 0",
            options = listOf("x = 3, 4", "x = 2, 6", "x = -3, -4", "x = 1, 12"),
            correctAnswer = 0,
            explanation = "(x - 3)(x - 4) = 0, следовательно x = 3 или x = 4",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 10,
            topicId = 4,
            question = "Каков дискриминант: x² + 4x + 4 = 0?",
            options = listOf("0", "8", "16", "-8"),
            correctAnswer = 0,
            explanation = "D = b² - 4ac = 16 - 16 = 0",
            difficulty = Difficulty.MEDIUM
        ),

        // Systems of Equations (Topic 5)
        Question(
            id = 11,
            topicId = 5,
            question = "Решите: x + y = 7 и x - y = 3",
            options = listOf("x = 5, y = 2", "x = 4, y = 3", "x = 3, y = 4", "x = 6, y = 1"),
            correctAnswer = 0,
            explanation = "Складываем уравнения: 2x = 10, x = 5. Затем y = 7 - 5 = 2",
            difficulty = Difficulty.MEDIUM
        ),

        // Factoring (Topic 6)
        Question(
            id = 12,
            topicId = 6,
            question = "Разложите: x² - 16",
            options = listOf("(x-4)(x+4)", "(x-8)(x+2)", "(x-4)²", "(x+4)²"),
            correctAnswer = 0,
            explanation = "Разность квадратов: a² - b² = (a-b)(a+b), следовательно x² - 16 = (x-4)(x+4)",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 13,
            topicId = 6,
            question = "Разложите: 2x² + 6x",
            options = listOf("2x(x + 3)", "2(x² + 3x)", "x(2x + 6)", "6x(x + 1)"),
            correctAnswer = 0,
            explanation = "НОД равен 2x: 2x² + 6x = 2x(x + 3)",
            difficulty = Difficulty.EASY
        ),

        // Functions (Topic 7)
        Question(
            id = 14,
            topicId = 7,
            question = "Если f(x) = 3x - 2, найдите f(5)",
            options = listOf("13", "15", "17", "11"),
            correctAnswer = 0,
            explanation = "f(5) = 3(5) - 2 = 15 - 2 = 13",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 15,
            topicId = 7,
            question = "Если f(x) = x² + 1, найдите f(-3)",
            options = listOf("8", "10", "-8", "4"),
            correctAnswer = 1,
            explanation = "f(-3) = (-3)² + 1 = 9 + 1 = 10",
            difficulty = Difficulty.EASY
        ),

        // Inequalities (Topic 8)
        Question(
            id = 16,
            topicId = 8,
            question = "Решите: 2x + 5 < 11",
            options = listOf("x < 3", "x > 3", "x < 8", "x > 8"),
            correctAnswer = 0,
            explanation = "2x < 11 - 5 = 6, x < 3",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 17,
            topicId = 8,
            question = "Решите: -4x ≥ 12",
            options = listOf("x ≥ -3", "x ≤ -3", "x ≥ 3", "x ≤ 3"),
            correctAnswer = 1,
            explanation = "Разделить на -4 и перевернуть: x ≤ -3",
            difficulty = Difficulty.MEDIUM
        ),

        // Exponents (Topic 9)
        Question(
            id = 18,
            topicId = 9,
            question = "Упростите: x⁴ × x³",
            options = listOf("x⁷", "x¹²", "x¹", "2x⁷"),
            correctAnswer = 0,
            explanation = "При умножении одинаковых оснований складывайте показатели: x⁴⁺³ = x⁷",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 19,
            topicId = 9,
            question = "Упростите: (x²)⁴",
            options = listOf("x⁶", "x⁸", "x²", "4x²"),
            correctAnswer = 1,
            explanation = "Степень степени: умножьте показатели: x²ˣ⁴ = x⁸",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 20,
            topicId = 9,
            question = "Что такое 5⁰?",
            options = listOf("0", "1", "5", "Не определено"),
            correctAnswer = 1,
            explanation = "Любое ненулевое число в степени 0 равно 1",
            difficulty = Difficulty.EASY
        ),

        // === НОВЫЕ ВОПРОСЫ ДЛЯ НОВЫХ ТЕМ ===

        // Fractions and Percentages (Topic 10)
        Question(
            id = 21,
            topicId = 10,
            question = "Упростите: (6x²)/(9x)",
            options = listOf("2x/3", "3x/2", "2x²/3", "x/3"),
            correctAnswer = 0,
            explanation = "6/9 = 2/3, x²/x = x, результат: (2x)/3",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 22,
            topicId = 10,
            question = "Цена товара повысилась на 15% и стала 230 руб. Какова была исходная цена?",
            options = listOf("200 руб.", "210 руб.", "195 руб.", "205 руб."),
            correctAnswer = 0,
            explanation = "Пусть x - исходная цена. Тогда 1.15x = 230, x = 230/1.15 = 200",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 23,
            topicId = 10,
            question = "Упростите: (x² - 1)/(x - 1)",
            options = listOf("x + 1", "x - 1", "1", "x² + 1"),
            correctAnswer = 0,
            explanation = "x² - 1 = (x-1)(x+1), сокращаем (x-1), остается x+1",
            difficulty = Difficulty.MEDIUM
        ),

        // Coordinate Plane (Topic 11)
        Question(
            id = 24,
            topicId = 11,
            question = "В какой четверти находится точка (-3, 4)?",
            options = listOf("I", "II", "III", "IV"),
            correctAnswer = 1,
            explanation = "x отрицательный, y положительный - это II четверть",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 25,
            topicId = 11,
            question = "Найдите расстояние между точками (1, 2) и (4, 6)",
            options = listOf("5", "4", "√13", "√20"),
            correctAnswer = 0,
            explanation = "d = √[(4-1)² + (6-2)²] = √[9 + 16] = √25 = 5",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 26,
            topicId = 11,
            question = "Найдите середину отрезка с концами в точках (2, 3) и (6, 9)",
            options = listOf("(4, 6)", "(3, 4)", "(5, 7)", "(4, 7)"),
            correctAnswer = 0,
            explanation = "M = ((2+6)/2, (3+9)/2) = (8/2, 12/2) = (4, 6)",
            difficulty = Difficulty.EASY
        ),

        // Rational Expressions (Topic 12)
        Question(
            id = 27,
            topicId = 12,
            question = "Упростите: (x² - 9)/(x² - 4x + 3)",
            options = listOf("(x+3)/(x-1)", "(x-3)/(x-1)", "x+3", "x-3"),
            correctAnswer = 0,
            explanation = "x²-9=(x-3)(x+3), x²-4x+3=(x-3)(x-1), сокращаем (x-3), получаем (x+3)/(x-1)",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 28,
            topicId = 12,
            question = "Какие ограничения у выражения: 1/(x² - 4)?",
            options = listOf("x ≠ ±2", "x ≠ 0", "x ≠ 2", "x ≠ -2"),
            correctAnswer = 0,
            explanation = "Знаменатель не может быть 0: x²-4=0 → x=±2, значит x≠±2",
            difficulty = Difficulty.MEDIUM
        ),

        // Pythagorean Theorem (Topic 13)
        Question(
            id = 29,
            topicId = 13,
            question = "В прямоугольном треугольнике катеты 5 и 12. Найдите гипотенузу.",
            options = listOf("13", "17", "15", "14"),
            correctAnswer = 0,
            explanation = "c² = 5² + 12² = 25 + 144 = 169, c = √169 = 13",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 30,
            topicId = 13,
            question = "Является ли треугольник со сторонами 8, 15, 17 прямоугольным?",
            options = listOf("Да", "Нет", "Недостаточно данных", "Только если он равнобедренный"),
            correctAnswer = 0,
            explanation = "Проверяем: 8² + 15² = 64 + 225 = 289, 17² = 289, значит 8²+15²=17² - прямоугольный",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 31,
            topicId = 13,
            question = "Если гипотенуза = 10, один катет = 6, найдите второй катет.",
            options = listOf("8", "9", "7", "√136"),
            correctAnswer = 0,
            explanation = "b² = c² - a² = 100 - 36 = 64, b = √64 = 8",
            difficulty = Difficulty.EASY
        ),

        // Sequences and Progressions (Topic 14)
        Question(
            id = 32,
            topicId = 14,
            question = "Найдите 10-й член арифметической прогрессии: 3, 7, 11, ...",
            options = listOf("39", "43", "35", "41"),
            correctAnswer = 0,
            explanation = "d=4, a₁=3, a₁₀ = a₁ + 9d = 3 + 9×4 = 3 + 36 = 39",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 33,
            topicId = 14,
            question = "Найдите сумму первых 10 членов арифметической прогрессии: 5, 9, 13, ...",
            options = listOf("230", "210", "250", "190"),
            correctAnswer = 0,
            explanation = "a₁=5, d=4, a₁₀=5+9×4=41, S₁₀ = 10/2×(5+41) = 5×46 = 230",
            difficulty = Difficulty.HARD
        ),
        Question(
            id = 34,
            topicId = 14,
            question = "Найдите 6-й член геометрической прогрессии: 2, 6, 18, ...",
            options = listOf("486", "162", "54", "1458"),
            correctAnswer = 0,
            explanation = "q=3, a₁=2, a₆ = a₁×q⁵ = 2×3⁵ = 2×243 = 486",
            difficulty = Difficulty.MEDIUM
        ),

        // Logarithms (Topic 15)
        Question(
            id = 35,
            topicId = 15,
            question = "Вычислите: log₂8",
            options = listOf("3", "2", "4", "1"),
            correctAnswer = 0,
            explanation = "log₂8 = x значит 2ˣ = 8, 2³ = 8, значит x=3",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 36,
            topicId = 15,
            question = "Упростите: log₃27 + log₃3",
            options = listOf("4", "3", "log₃81", "2"),
            correctAnswer = 0,
            explanation = "log₃27 = 3, log₃3 = 1, 3+1=4. Или: log₃27 + log₃3 = log₃(27×3) = log₃81 = 4",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 37,
            topicId = 15,
            question = "Решите: log₅x = 2",
            options = listOf("25", "10", "32", "5"),
            correctAnswer = 0,
            explanation = "log₅x = 2 значит x = 5² = 25",
            difficulty = Difficulty.EASY
        ),

        // Trigonometry (Topic 16)
        Question(
            id = 38,
            topicId = 16,
            question = "В прямоугольном треугольнике гипотенуза = 13, катет = 5. Найдите sin α (α - угол напротив катета 5).",
            options = listOf("5/13", "12/13", "5/12", "12/5"),
            correctAnswer = 0,
            explanation = "sin α = противолежащий/гипотенуза = 5/13",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 39,
            topicId = 16,
            question = "Упростите: sin²θ + cos²θ",
            options = listOf("1", "0", "sin²θ", "cos²θ"),
            correctAnswer = 0,
            explanation = "Это основное тригонометрическое тождество: sin²θ + cos²θ = 1",
            difficulty = Difficulty.EASY
        ),

        // Combinatorics and Probability (Topic 17)
        Question(
            id = 40,
            topicId = 17,
            question = "Сколько способов рассадить 4 человек на 4 стульях?",
            options = listOf("24", "16", "12", "8"),
            correctAnswer = 0,
            explanation = "Это перестановки: P₄ = 4! = 4×3×2×1 = 24",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 41,
            topicId = 17,
            question = "Сколькими способами выбрать 2 книги из 5?",
            options = listOf("10", "20", "25", "15"),
            correctAnswer = 0,
            explanation = "Это сочетания: C₅² = 5!/(2!×3!) = (5×4)/(2×1) = 10",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 42,
            topicId = 17,
            question = "Какова вероятность выпадения орла при одном подбрасывании монеты?",
            options = listOf("1/2", "1/4", "1", "0"),
            correctAnswer = 0,
            explanation = "Всего 2 равновозможных исхода (орел или решка), благоприятных - 1, P = 1/2",
            difficulty = Difficulty.EASY
        ),

        // Sets (Topic 18)
        Question(
            id = 43,
            topicId = 18,
            question = "Даны A = {1,2,3}, B = {3,4,5}. Найдите A ∪ B.",
            options = listOf("{1,2,3,4,5}", "{3}", "{1,2,4,5}", "∅"),
            correctAnswer = 0,
            explanation = "Объединение включает все элементы из A и B: {1,2,3,4,5}",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 44,
            topicId = 18,
            question = "Даны A = {1,2,3,4}, B = {3,4,5,6}. Найдите A ∩ B.",
            options = listOf("{3,4}", "{1,2,5,6}", "{1,2,3,4,5,6}", "{1,2}"),
            correctAnswer = 0,
            explanation = "Пересечение включает элементы, которые есть и в A, и в B: {3,4}",
            difficulty = Difficulty.EASY
        ),

        // Complex Numbers (Topic 19)
        Question(
            id = 45,
            topicId = 19,
            question = "Найдите сумму: (3 + 2i) + (1 - 4i)",
            options = listOf("4 - 2i", "4 + 6i", "2 - 2i", "3 - 4i"),
            correctAnswer = 0,
            explanation = "Складываем действительные части: 3+1=4, мнимые: 2i-4i=-2i, результат: 4-2i",
            difficulty = Difficulty.EASY
        ),
        Question(
            id = 46,
            topicId = 19,
            question = "Найдите модуль числа: 3 + 4i",
            options = listOf("5", "7", "√7", "√25"),
            correctAnswer = 0,
            explanation = "|3+4i| = √(3² + 4²) = √(9 + 16) = √25 = 5",
            difficulty = Difficulty.MEDIUM
        ),
        Question(
            id = 47,
            topicId = 19,
            question = "Что такое мнимая единица i?",
            options = listOf("i² = -1", "i = √1", "i = -1", "i = 0"),
            correctAnswer = 0,
            explanation = "По определению i - мнимая единица, i² = -1",
            difficulty = Difficulty.EASY
        ),

        // Дополнительные сложные вопросы
        Question(
            id = 48,
            topicId = 4,
            question = "Решите квадратное уравнение: 2x² - 5x - 3 = 0",
            options = listOf("x = 3, x = -0.5", "x = 2, x = 1.5", "x = -3, x = 0.5", "x = 1, x = -1.5"),
            correctAnswer = 0,
            explanation = "D = 25 + 24 = 49, x = (5±7)/4, x₁=3, x₂=-0.5",
            difficulty = Difficulty.HARD
        ),
        Question(
            id = 49,
            topicId = 5,
            question = "Решите систему: 2x + 3y = 8, 4x - y = 6",
            options = listOf("x = 2, y = 2", "x = 1, y = 2", "x = 2, y = 1", "x = 3, y = 1"),
            correctAnswer = 2,
            explanation = "Из второго: y = 4x-6. Подставляем в первое: 2x+3(4x-6)=8 → 14x=26 → x=2, y=4×2-6=2",
            difficulty = Difficulty.HARD
        ),
        Question(
            id = 50,
            topicId = 9,
            question = "Упростите: (x²y³)⁴ × (x³y²)²",
            options = listOf("x¹⁴y¹⁶", "x⁵y⁵", "x⁸y¹²", "x⁷y⁸"),
            correctAnswer = 0,
            explanation = "(x²y³)⁴ = x⁸y¹², (x³y²)² = x⁶y⁴, произведение: x⁸⁺⁶y¹²⁺⁴ = x¹⁴y¹⁶",
            difficulty = Difficulty.HARD
        )
    )

    fun getQuestionsByTopic(topicId: Int): List<Question> =
        questions.filter { it.topicId == topicId }

    fun getQuestionsByDifficulty(difficulty: Difficulty): List<Question> =
        questions.filter { it.difficulty == difficulty }

    fun getRandomQuestions(count: Int): List<Question> =
        questions.shuffled().take(count)
}