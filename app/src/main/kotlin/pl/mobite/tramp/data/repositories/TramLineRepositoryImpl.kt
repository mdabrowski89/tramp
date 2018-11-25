package pl.mobite.tramp.data.repositories

import pl.mobite.tramp.data.local.repositories.TramLineLocalRepository
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc


class TramLineRepositoryImpl(
    private val tramLineLocalRepository: TramLineLocalRepository
): TramLineRepository {

    override fun getTramLineFromLocal(tramLineDesc: TramLineDesc): TramLine? {
        var tramLine = tramLineLocalRepository.getTramLineFromDb(tramLineDesc)
        if (tramLine == null) {
            tramLine = tramLineLocalRepository.getTramLineFromJson(tramLineDesc)
            if (tramLine != null) {
                tramLineLocalRepository.storeTramLineInDb(tramLineDesc, tramLine)
            }
        }
        return tramLine
    }

    override fun getTramLineFromRemote(tramLineDesc: TramLineDesc): TramLine {
        throw Throwable("Remote repository not implemented")
    }
}