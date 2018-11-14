package pl.mobite.tramp.data.repositories

import io.reactivex.Maybe
import io.reactivex.Single
import pl.mobite.tramp.data.local.repositories.TramLineLocalRepository
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


class TramLineRepositoryImpl(
    private val tramLineLocalRepository: TramLineLocalRepository
): TramLineRepository {

    override fun getTramLineFromLocal(tramLineDesc: TramLineDesc): Maybe<TramLine> {
        return tramLineLocalRepository
            .getTramLineFromDb(tramLineDesc)
            .switchIfEmpty(
                tramLineLocalRepository
                    .getTramLineFromJson(tramLineDesc)
                    .flatMap { tramLine -> tramLineLocalRepository
                        .storeTramLineInDb(tramLineDesc, tramLine)
                        .andThen(Maybe.just(tramLine))
                    }
            )
    }

    override fun getTramLineFromRemote(tramLineDesc: TramLineDesc): Single<TramLine> {
        return Single.error(Throwable("Remote repository not implemented"))
    }
}