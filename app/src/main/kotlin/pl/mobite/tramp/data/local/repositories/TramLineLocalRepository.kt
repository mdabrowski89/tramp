package pl.mobite.tramp.data.local.repositories

import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


interface TramLineLocalRepository {

    fun getTramStops(tramLineDesc: TramLineDesc): Single<TramLine>
}