package pl.mobite.tramp.data.repositories

import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


interface TramLineRepository {

    fun getTramLine(tramLineDesc: TramLineDesc): Single<TramLine>
}