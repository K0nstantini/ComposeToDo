package com.grommade.composetodo.use_cases

import com.grommade.composetodo.data.entity.RandomTask
import com.grommade.composetodo.data.repos.RepoTask
import javax.inject.Inject

interface PopulateDBWithTasks {
    suspend operator fun invoke()
}

class PopulateDBWithTasksImpl @Inject constructor(
    private val repoSingleTask: RepoTask
) : PopulateDBWithTasks {
    override suspend fun invoke() {

        repoSingleTask.deleteAllTasks()

        val single = RandomTask.SingleTask()
        val routine = RandomTask(name = "Быт", group = true).save()
        RandomTask(name = "Убрать на столе", parent = routine).save()
        RandomTask(name = "Убраться в отделении на столе", parent = routine).save()
        RandomTask(name = "Убраться в верхнем ящике стола", parent = routine).save()
        RandomTask(name = "Убраться в среднем ящике стола", parent = routine).save()
        RandomTask(name = "Разобрать пакет под стулом", parent = routine).save()
        RandomTask(name = "Разметить турник", parent = routine).save()
        RandomTask(name = "Заказ Aliexpress (тестер, ключ и пр.)", parent = routine).save()
        RandomTask(name = "Заказ/Выбор IHerb", parent = routine).save()
        RandomTask(name = "Компьютер в зале", parent = routine).save()
        RandomTask(name = "Сиденье унитаза", parent = routine).save()
        RandomTask(name = "Почистить кофемашину", parent = routine).save()
        RandomTask(name = "Сходить в банк", parent = routine).save()
        RandomTask(name = "Разобраться с пылесосом", parent = routine).save()

        val pc = RandomTask(name = "Компьютер, телефон и пр.", group = true).save()
        RandomTask(name = "Придумать систему бэкапов", parent = pc).save()
        RandomTask(name = "Вкладки Chrome (ноут)", parent = pc).save()
        RandomTask(name = "Вкладки Chrome (комп)", parent = pc).save()
        RandomTask(name = "Рабочий стол (ноут)", parent = pc).save()
        RandomTask(name = "Рабочий стол (комп)", parent = pc).save()
        RandomTask(name = "Разобраться с телефоном, бэкап и пр.", parent = pc).save()
        RandomTask(name = "Купить что-нибудь в форе", single = single.copy(deadlineDays = 72), parent = pc).save()

        val poker = RandomTask(name = "Покер", group = true).save()
        RandomTask(name = "Сыграть в покер", parent = poker).save()
        RandomTask(name = "Кэшаут Старзы", parent = poker).save()

        val music = RandomTask(name = "Музыка", group = true).save()

        val mOthers = RandomTask(name = "Прочее", parent = music, group = true).save()
        RandomTask(name = "Подключить синтезатор", parent = mOthers).save()
        RandomTask(name = "Найти/заказать дисковод/дискеты", parent = mOthers).save()
        RandomTask(name = "Выбрать 'песню' для аранжировки", parent = mOthers).save()

        val mPractice = RandomTask(name = "Практика", parent = music, group = true).save()
        RandomTask(name = "Сольфеджио", parent = mPractice).save()
        RandomTask(name = "Электрогитара", parent = mPractice).save()
        RandomTask(name = "Тренажер слуха", parent = mPractice).save()

        val mTheory = RandomTask(name = "Теория", parent = music, group = true).save()
        RandomTask(name = "Дослушать Баха", parent = mTheory).save()
        RandomTask(name = "Музыкофилия 30 мин.", parent = mTheory).save()


        val english = RandomTask(name = "Английский", group = true).save()
        RandomTask(name = "Дочитать главу HPMOR", parent = english).save()
        RandomTask(name = "Досмотреть форд против феррари", parent = english).save()
        RandomTask(name = "Серия How I Met Your Mother", parent = english).save()
        RandomTask(name = "Bill Perkins 1 chapter or 30 min", parent = english).save()

        
    }
    
    private suspend fun RandomTask.save() = repoSingleTask.saveTask(this)
}