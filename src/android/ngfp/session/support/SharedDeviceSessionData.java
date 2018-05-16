package com.homedepot.ngfp.session.support;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable session data object to be passed over IPC to other applications that need (and are
 * permissioned for) this data.  Only session fields that need to be shared with other applications
 * will be captured here.  This does not necessarily represent all the data within a device's
 * session.
 *
 * Note that we're including locale in this payload as well as in the user session payload.  There will
 * be cases where interfacing applications will primarily require device information, but will need
 * the locale for presentation purposes.  Including it here allows those applications to request this
 * payload <i>only</i>, and to avoid declaring permissions for the user session payload separate from
 * this device session payload.
 */
public class SharedDeviceSessionData implements Parcelable {
    private final boolean empty;
    private final String storeNumber;
    private final String extensionNumber;
    private final String voipRegistrationCode;
    private final List<String> pttChannelList = new ArrayList<String>();
    private final String deviceId;
    private final String deviceModelNumber;
    private final String ipAddress;
    private final String locale;
    private final String launcherVersion;
    private final boolean qa;

    /**
     * Empty constructor.  Used when no session data exists.
     */
    public SharedDeviceSessionData() {
        this.empty = true;
        this.storeNumber = null;
        this.extensionNumber = null;
        this.voipRegistrationCode = null;
        this.deviceId = null;
        this.deviceModelNumber = null;
        this.ipAddress = null;
        this.locale = null;
        this.launcherVersion = null;
        this.qa = false;
    }

    /**
     * Parameterized Constructor.  This is the only way to create a SharedDeviceSessionData object.
     * Fields are immutable after being injected through construction.
     *
     * @param storeNumber - the store number.
     * @param deviceId - the device's unique identifier.
     * @param deviceModelNumber - the device's model number.
     * @param ipAddress - the device's ip address.
     * @param locale - the device's locale - either selected by the user or default for the device.
     */
    public SharedDeviceSessionData(String storeNumber, String extensionNumber, String voipRegistrationCode,
                                   List<String> pttChannelList, String deviceId,
                                   String deviceModelNumber, String ipAddress, String locale,
                                   String launcherVersion, boolean qa) {
        this.empty = false;
        this.storeNumber = storeNumber;
        this.extensionNumber = extensionNumber;
        this.pttChannelList.addAll(pttChannelList);
        this.voipRegistrationCode = voipRegistrationCode;
        this.deviceId = deviceId;
        this.deviceModelNumber = deviceModelNumber;
        this.ipAddress = ipAddress;
        this.locale = locale;
        this.launcherVersion = launcherVersion;
        this.qa = qa;
    }

    /**
     * Parcel constructor.  This will be used when this object is being 'parceled' / 'deparceled'
     * through IPC (Android serialization).
     * @param parcel
     */
    public SharedDeviceSessionData(Parcel parcel) {
        this.empty = (parcel.readByte() != 0);

        if(!this.empty) {
            this.storeNumber = parcel.readString();
            this.extensionNumber = parcel.readString();
            parcel.readStringList(this.pttChannelList);
            this.voipRegistrationCode = parcel.readString();
            this.deviceId = parcel.readString();
            this.deviceModelNumber = parcel.readString();
            this.ipAddress = parcel.readString();
            this.locale = parcel.readString();
            this.launcherVersion = parcel.readString();
            this.qa = (parcel.readByte() != 0);
        } else {
            this.storeNumber = null;
            this.extensionNumber = null;
            this.voipRegistrationCode = null;
            this.deviceId = null;
            this.deviceModelNumber = null;
            this.ipAddress = null;
            this.locale = null;
            this.launcherVersion = null;
            this.qa = false;
        }
    }

    /**
     * Gets the store number.
     * @return A string representation of the current store number.
     */
    public String getStoreNumber() {
        return storeNumber;
    }

    /**
     * Gets the device's extension, if available.
     * @return the device's extension value, if available.
     */
    public String getExtensionNumber() {
        return extensionNumber;
    }

    /**
     * Gets the device identifier.
     * @return the current device's unique identifier.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Gets the device type.
     * @return a string representation of the current device's model number / type
     */
    public String getDeviceModelNumber() {
        return deviceModelNumber;
    }

    /**
     * Gets the IP address of the device.
     * @return a string representation of the current device's IP address.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the device or user's chosen locale.
     * @return the device or user's chosen locale.
     */
    public String getLocale() {
        return locale;
    }

    public String getVoipRegistrationCode() {
        return voipRegistrationCode;
    }

    public List<String> getPttChannelList() {
        return pttChannelList;
    }

    public String getLauncherVersion() {
        return launcherVersion;
    }

    public boolean isQa() {
        return qa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharedDeviceSessionData that = (SharedDeviceSessionData) o;

        if (empty != that.empty) return false;
        if (qa != that.qa) return false;
        if (deviceId != null ? !deviceId.equals(that.deviceId) : that.deviceId != null)
            return false;
        if (deviceModelNumber != null ? !deviceModelNumber.equals(that.deviceModelNumber) : that.deviceModelNumber != null)
            return false;
        if (extensionNumber != null ? !extensionNumber.equals(that.extensionNumber) : that.extensionNumber != null)
            return false;
        if (ipAddress != null ? !ipAddress.equals(that.ipAddress) : that.ipAddress != null)
            return false;
        if (launcherVersion != null ? !launcherVersion.equals(that.launcherVersion) : that.launcherVersion != null)
            return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
        if (pttChannelList != null ? !pttChannelList.equals(that.pttChannelList) : that.pttChannelList != null)
            return false;
        if (storeNumber != null ? !storeNumber.equals(that.storeNumber) : that.storeNumber != null)
            return false;
        if (voipRegistrationCode != null ? !voipRegistrationCode.equals(that.voipRegistrationCode) : that.voipRegistrationCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (empty ? 1 : 0);
        result = 31 * result + (storeNumber != null ? storeNumber.hashCode() : 0);
        result = 31 * result + (extensionNumber != null ? extensionNumber.hashCode() : 0);
        result = 31 * result + (voipRegistrationCode != null ? voipRegistrationCode.hashCode() : 0);
        result = 31 * result + (pttChannelList != null ? pttChannelList.hashCode() : 0);
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + (deviceModelNumber != null ? deviceModelNumber.hashCode() : 0);
        result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (launcherVersion != null ? launcherVersion.hashCode() : 0);
        result = 31 * result + (qa ? 1 : 0);
        return result;
    }

// Methods / statics for Parcelable contract follow below.

    /**
     * CREATOR is part of the Parcelable contract.  This will be used by the Android system to
     * serialize / deserialize session data.
     */
    public static final Creator<SharedDeviceSessionData> CREATOR =
            new Creator<SharedDeviceSessionData>() {

                /*
                Elements are 'de-parceled' in the order that they're defined in this class.
                 */
                @Override
                public SharedDeviceSessionData createFromParcel(Parcel parcel) {
                    return new SharedDeviceSessionData(parcel);
                }

                @Override
                public SharedDeviceSessionData[] newArray(int i) {
                    return new SharedDeviceSessionData[i];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    /*
    Elements are 'parceled' in the order that they're defined in this class.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (this.empty ? 1 : 0));

        //keep our parcelable payload as small as possible if the session is not available.
        if(!this.empty) {
            parcel.writeString(this.storeNumber);
            parcel.writeString(this.extensionNumber);
            parcel.writeStringList(this.pttChannelList);
            parcel.writeString(this.voipRegistrationCode);
            parcel.writeString(this.deviceId);
            parcel.writeString(this.deviceModelNumber);
            parcel.writeString(this.ipAddress);
            parcel.writeString(this.locale);
            parcel.writeString(this.launcherVersion);
            parcel.writeByte((byte) (this.qa ? 1 : 0));
        }
    }
}
