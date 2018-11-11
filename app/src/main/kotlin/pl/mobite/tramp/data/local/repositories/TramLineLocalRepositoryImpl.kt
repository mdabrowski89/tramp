package pl.mobite.tramp.data.local.repositories

import io.reactivex.Single
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.data.repositories.models.TramStop


class TramLineLocalRepositoryImpl: TramLineLocalRepository {

    override fun getTramStops(tramLineDesc: TramLineDesc): Single<TramLine> {
        // TODO: read tram stops from assets
        return Single.fromCallable {
            TramLine(tramLineDesc, listOf(TramStop(1, "Name", 10.0, 20.0)))
        }
    }

}