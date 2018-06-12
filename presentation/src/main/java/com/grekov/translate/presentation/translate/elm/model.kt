package com.grekov.translate.presentation.translate.elm

import com.grekov.translate.domain.elm.Cmd
import com.grekov.translate.domain.elm.Msg
import com.grekov.translate.domain.model.Lang
import com.grekov.translate.domain.model.Phrase
import com.grekov.translate.presentation.langs.view.controller.LangsController

data class NavigateToLangsMsg(val from: Boolean) : Msg()
data class ChangeLangsMsg(val lang: Lang, val from: Boolean) : Msg()
data class LangsByCodeMsg(val from: Lang, val to: Lang) : Msg()
object RotateLangsMsg : Msg()
data class TranslateResultMsg(val text: Phrase) : Msg()
data class LangsFromPrefsMsg(val from: Lang, val to: Lang) : Msg()
object DropTextMsg : Msg()
object SaveToFavoritesMsg : Msg()
object RemoveFromFavoritesMsg : Msg()
class FavoriteCheckResultMsg(val phrase: String, val from: Lang, val to: Lang, val favorite: Boolean) : Msg()
data class TextChangeMsg(val text: String) : Msg()
data class PhraseSelectMsg(val phrase: Phrase) : Msg()
object TranslateMsg : Msg()

object RetrieveLangsFromPrefsCmd : Cmd()
data class SaveCurrentLangsCmd(val from: Lang, val to: Lang) : Cmd()
data class TranslateCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
data class MakeFavoriteCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
data class MakeNotFavoriteCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
data class CheckFavoriteCmd(val text: String, val from: Lang, val to: Lang) : Cmd()
data class GetLangsByCodeCmd(val langsLiteral: String) : Cmd()
data class GoToLangsCmd(val from: Boolean, val langsSelectCallback: LangsController.TargetLangSelectListener) :
    Cmd()
