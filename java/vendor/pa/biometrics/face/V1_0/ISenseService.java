package vendor.pa.biometrics.face.V1_0;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISenseService extends IInterface {

    public static abstract class Stub extends Binder implements ISenseService {
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "vendor.pa.biometrics.face.V1_0.ISenseService");
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        authenticate(parcel.readLong());
                        return true;
                    case 2:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        cancel();
                        return true;
                    case 3:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        enroll(parcel.createByteArray(), parcel.readInt(), parcel.createIntArray());
                        return true;
                    case 4:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        int enumerate = enumerate();
                        parcel2.writeNoException();
                        parcel2.writeInt(enumerate);
                        return true;
                    case 5:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        long generateChallenge = generateChallenge(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeLong(generateChallenge);
                        return true;
                    case 6:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        int authenticatorId = getAuthenticatorId();
                        parcel2.writeNoException();
                        parcel2.writeInt(authenticatorId);
                        return true;
                    case 7:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        remove(parcel.readInt());
                        return true;
                    case 8:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        resetLockout(parcel.createByteArray());
                        return true;
                    case 9:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        int revokeChallenge = revokeChallenge();
                        parcel2.writeNoException();
                        parcel2.writeInt(revokeChallenge);
                        return true;
                    case 10:
                        parcel.enforceInterface("vendor.pa.biometrics.face.V1_0.ISenseService");
                        setCallback(vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver.Stub.asInterface(parcel.readStrongBinder()));
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString("vendor.pa.biometrics.face.V1_0.ISenseService");
                return true;
            }
        }
    }

    void authenticate(long j) throws RemoteException;

    void cancel() throws RemoteException;

    void enroll(byte[] bArr, int i, int[] iArr) throws RemoteException;

    int enumerate() throws RemoteException;

    long generateChallenge(int i) throws RemoteException;

    int getAuthenticatorId() throws RemoteException;

    void remove(int i) throws RemoteException;

    void resetLockout(byte[] bArr) throws RemoteException;

    int revokeChallenge() throws RemoteException;

    void setCallback(ISenseServiceReceiver iSenseServiceReceiver) throws RemoteException;
}
