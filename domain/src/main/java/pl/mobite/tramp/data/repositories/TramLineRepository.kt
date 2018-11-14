package pl.mobite.tramp.data.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


interface TramLineRepository {

    fun getTramLineFromLocal(tramLineDesc: TramLineDesc): Maybe<TramLine>

    fun getTramLineFromRemote(tramLineDesc: TramLineDesc): Single<TramLine>

}