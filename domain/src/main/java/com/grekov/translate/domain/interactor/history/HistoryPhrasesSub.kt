package com.grekov.translate.domain.interactor.history

import com.grekov.translate.domain.elm.Msg
import com.grekov.translate.domain.interactor.base.ElmSubscription
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.domain.repository.ITranslateRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

data class HistoryLoadedMsg(val phrases: List<Phrase>) : Msg()

data class HistoryPhrasesParams(val searchText: String, val isFavorite: Boolean?)

class HistoryPhrasesSub
@Inject constructor(@Named("Output") outputScheduler: Scheduler,
                    private val translateRepository: ITranslateRepository)
    : ElmSubscription<HistoryLoadedMsg, HistoryPhrasesParams>(outputScheduler) {


    override fun buildObservable(params: HistoryPhrasesParams): Observable<HistoryLoadedMsg> {
        return if (params.isFavorite == true) {
            translateRepository.getFavoritePhrases(params.searchText).map { phrases -> HistoryLoadedMsg(phrases) }
        } else {
            translateRepository.getPhrasesHistory(params.searchText).map { phrases -> HistoryLoadedMsg(phrases) }
        }
    }

}