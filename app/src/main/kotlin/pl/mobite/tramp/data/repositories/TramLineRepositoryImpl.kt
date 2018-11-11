package pl.mobite.tramp.data.repositories

import io.reactivex.Single
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepository
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


class TramLineRepositoryImpl(
    private val tramLineLocalRepository: TramLineLocalRepository
): TramLineRepository {

    override fun getTramStops(tramLineDesc: TramLineDesc): Single<TramLine> {
        return tramLineLocalRepository.getTramStops(tramLineDesc)
    }
}