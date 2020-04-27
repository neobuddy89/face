package vendor.pa.biometrics.face.V1_0;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface ISenseServiceReceiver extends IInterface {

    public static abstract class Stub extends Binder implements ISenseServiceReceiver {

        private static class Proxy implements ISenseServiceReceiver {
            public static ISenseServiceReceiver sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onEnrollResult(int i, int i2, int i3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeInt(i3);
                    if (this.mRemote.transact(1, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onEnrollResult(i, i2, i3);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onAuthenticated(int i, int i2, byte[] bArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    obtain.writeByteArray(bArr);
                    if (this.mRemote.transact(2, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onAuthenticated(i, i2, bArr);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onError(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    if (this.mRemote.transact(4, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onError(i, i2);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onRemoved(int[] iArr, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
                    obtain.writeIntArray(iArr);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(5, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onRemoved(iArr, i);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onEnumerate(int[] iArr, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
                    obtain.writeIntArray(iArr);
                    obtain.writeInt(i);
                    if (this.mRemote.transact(6, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onEnumerate(iArr, i);
                    }
                } finally {
                    obtain.recycle();
                }
            }

            public void onLockoutChanged(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
                    obtain.writeLong(j);
                    if (this.mRemote.transact(7, obtain, null, 1) || Stub.getDefaultImpl() == null) {
                        obtain.recycle();
                    } else {
                        Stub.getDefaultImpl().onLockoutChanged(j);
                    }
                } finally {
                    obtain.recycle();
                }
            }
        }

        public static ISenseServiceReceiver asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("vendor.pa.biometrics.face.V1_0.ISenseServiceReceiver");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ISenseServiceReceiver)) {
                return new Proxy(iBinder);
            }
            return (ISenseServiceReceiver) queryLocalInterface;
        }

        public static ISenseServiceReceiver getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void onAuthenticated(int i, int i2, byte[] bArr) throws RemoteException;

    void onEnrollResult(int i, int i2, int i3) throws RemoteException;

    void onEnumerate(int[] iArr, int i) throws RemoteException;

    void onError(int i, int i2) throws RemoteException;

    void onLockoutChanged(long j) throws RemoteException;

    void onRemoved(int[] iArr, int i) throws RemoteException;
}
