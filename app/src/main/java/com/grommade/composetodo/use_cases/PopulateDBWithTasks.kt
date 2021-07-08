package com.grommade.composetodo.use_cases

import com.grommade.composetodo.data.entity.Task
import com.grommade.composetodo.data.repos.RepoSingleTask
import javax.inject.Inject

interface PopulateDBWithTasks {
    suspend operator fun invoke()
}

class PopulateDBWithTasksImpl @Inject constructor(
    private val repoSingleTask: RepoSingleTask
) : PopulateDBWithTasks {
    override suspend fun invoke() {

        repoSingleTask.deleteAllTasks()

        val single = Task.SingleTask()
        val routine = Task(name = "Быт", group = true).save()
        Task(name = "Убрать на столе", parent = routine).save()
        Task(name = "Убраться в отделении на столе", parent = routine).save()
        Task(name = "Убраться в верхнем ящике стола", parent = routine).save()
        Task(name = "Убраться в среднем ящике стола", parent = routine).save()
        Task(name = "Убраться в нижнем ящике стола", parent = routine).save()
        Task(name = "Разобрать пакет под стулом", parent = routine).save()
        Task(name = "Разметить турник", parent = routine).save()
        Task(name = "Заказ Aliexpress (тестер, ключ и пр.)", parent = routine).save()
        Task(name = "Заказ/Выбор IHerb", parent = routine).save()
        Task(name = "Компьютер в зале", parent = routine).save()
        Task(name = "Сиденье унитаза", parent = routine).save()
        Task(name = "Почистить кофемашину", parent = routine).save()
        Task(name = "Сходить в банк", parent = routine).save()
        Task(name = "Разобраться с пылесосом", parent = routine).save()

        val pc = Task(name = "Компьютер, телефон и пр.", group = true).save()
        Task(name = "Придумать систему бэкапов", parent = pc).save()
        Task(name = "Вкладки Chrome (ноут)", parent = pc).save()
        Task(name = "Вкладки Chrome (комп)", parent = pc).save()
        Task(name = "Рабочий стол (ноут)", parent = pc).save()
        Task(name = "Рабочий стол (комп)", parent = pc).save()
        Task(name = "Разобраться с телефоном, бэкап и пр.", parent = pc).save()
        Task(name = "Купить что-нибудь в форе", single = single.copy(deadlineDays = 72), parent = pc).save()

        val poker = Task(name = "Покер", group = true).save()
        Task(name = "Сыграть в покер", parent = poker).save()
        Task(name = "Кэшаут Старзы", parent = poker).save()

        val music = Task(name = "Музыка", group = true).save()

        val mOthers = Task(name = "Прочее", parent = music, group = true).save()
        Task(name = "Подключить синтезатор", parent = mOthers).save()
        Task(name = "Найти/заказать дисковод/дискеты", parent = mOthers).save()
        Task(name = "Выбрать 'песню' для аранжировки", parent = mOthers).save()

        val mPractice = Task(name = "Практика", parent = music, group = true).save()
        Task(name = "Сольфеджио", parent = mPractice).save()
        Task(name = "Электрогитара", parent = mPractice).save()
        Task(name = "Тренажер слуха", parent = mPractice).save()

        val mTheory = Task(name = "Теория", parent = music, group = true).save()
        Task(name = "Дослушать Баха", parent = mTheory).save()
        Task(name = "Музыкофилия 30 мин.", parent = mTheory).save()


        val english = Task(name = "Английский", group = true).save()
        Task(name = "Дочитать главу HPMOR", parent = english).save()
        Task(name = "Досмотреть форд против феррари", parent = english).save()
        Task(name = "Серия How I Met Your Mother", parent = english).save()
        Task(name = "Bill Perkins 1 chapter or 30 min", parent = english).save()

        
    }
    
    private suspend fun Task.save() = repoSingleTask.saveTask(this)
}