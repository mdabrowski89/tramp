package pl.mobite.tramp.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


interface TramLineLocalRepository {

    fun getTramLineFromDb(tramLineDesc: TramLineDesc): Maybe<TramLine>

    fun getTramLineFromJson(tramLineDesc: TramLineDesc): Maybe<TramLine>

    fun storeTramLineInDb(tramLine: TramLine): Completable
}