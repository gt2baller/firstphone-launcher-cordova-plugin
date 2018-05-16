package com.homedepot.ngfp.session.support;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable session data object to be passed over IPC to other applications that need (and are
 * permissioned for) this data.  This represents the user's session data elements that can be
 * shared with other applications, but does NOT contain all user session data.
 */
public class SharedUserSessionData implements Parcelable {

    private final boolean empty;
    private final String departmentNumber;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String locationNumber;
    private final String locationType;
    private final String locale;
    private final String userId;
    private final String userType;
    private final int imsUserLevel;
    private final String thdSsoToken;
    private final List<String> ldapGroups = new ArrayList<String>();

    /**
     * Empty default constructor - should be used to create a session data payload when no user
     * session actually exists.  The empty flag will be set to true.
     */
    public SharedUserSessionData() {
        this.empty = true;
        this.departmentNumber = null;
        this.firstName = null;
        this.middleName = null;
        this.lastName = null;
        this.locationNumber = null;
        this.locationType = null;
        this.locale = null;
        this.userId = null;
        this.userType = null;
        this.imsUserLevel = 0;
        this.thdSsoToken = null;
    }

    /**
     * Parameterized constructor to create an immutable SharedUserSessionData object.
     *
     * @param departmentNumber - the user's department number.
     * @param firstName - the user's first name.
     * @param middleName - the user's middle name.
     * @param lastName - the user's last name.
     * @param locationNumber - the user's location.
     * @param locationType - the user's location type.
     * @param locale - the user's preferred locale. represented as a string to remain parcelable.
     * @param userId - the user's unique identifier.
     * @param userType - the user's type.
     * @param imsUserLevel - the user's IMS user level.
     * @param thdSsoToken - the user's unique THD SSO token.
     */
    public SharedUserSessionData(String departmentNumber, String firstName, String middleName,
                                 String lastName, String locationNumber, String locationType,
                                 String locale, String userId, String userType, int imsUserLevel,
                                 String thdSsoToken, List<String> ldapGroups) {
        this.empty = false;
        this.departmentNumber = departmentNumber;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.locationNumber = locationNumber;
        this.locationType = locationType;
        this.locale = locale;
        this.userId = userId;
        this.userType = userType;
        this.imsUserLevel = imsUserLevel;
        this.thdSsoToken = thdSsoToken;
        this.ldapGroups.addAll(ldapGroups);
    }

    /**
     * Parcel constructor.  This is used when objects of this type are shared through IPC and
     * 'parceled' / 'deparceled' between processes.
     * @param parcel
     */
    public SharedUserSessionData(Parcel parcel) {
        this.empty = parcel.readByte() != 0;

        if(!this.empty) {
            this.departmentNumber = parcel.readString();
            this.firstName = parcel.readString();
            this.middleName = parcel.readString();
            this.lastName = parcel.readString();
            this.locationNumber = parcel.readString();
            this.locationType = parcel.readString();
            this.locale = parcel.readString();
            this.userId = parcel.readString();
            this.userType = parcel.readString();
            this.imsUserLevel = parcel.readInt();
            this.thdSsoToken = parcel.readString();
            parcel.readStringList(this.ldapGroups);
        } else {
            this.departmentNumber = null;
            this.firstName = null;
            this.middleName = null;
            this.lastName = null;
            this.locationNumber = null;
            this.locationType = null;
            this.locale = null;
            this.userId = null;
            this.userType = null;
            this.imsUserLevel = 0;
            this.thdSsoToken = null;
        }
    }

    /**
     * Gets the department number.
     * @return
     */
    public String getDepartmentNumber() {
        return departmentNumber;
    }

    /**
     * Gets the user's first name.
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get's the user's middle name.
     * @return
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Get's the user's last name.
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get's the user's location.
     * @return
     */
    public String getLocationNumber() {
        return locationNumber;
    }

    /**
     * Get's the user's location type.
     * @return
     */
    public String getLocationType() {
        return locationType;
    }

    /**
     * Get's the user's preferred locale, as a String.
     * @return
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Get's the user's ID
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Get's the user's type.
     * @return
     */
    public String getUserType() {
        return userType;
    }

    /**
     * Get's the user's level.
     * @return
     */
    public int getImsUserLevel() {
        return imsUserLevel;
    }

    /**
     * Get's the user's THD SSO Token.
     * @return
     */
    public String getThdSsoToken() {
        return thdSsoToken;
    }

    /**
     * Get's the user's assigned ldap groups.
     * @return a list of ldap groups, each represented as a string.
     */
    public List<String> getLdapGroups() {
        return ldapGroups;
    }

    /*
        Auto-generated equals method.
         */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharedUserSessionData that = (SharedUserSessionData) o;
        if (empty != that.empty)
            return false;
        if (imsUserLevel != that.imsUserLevel) return false;
        if (departmentNumber != null ? !departmentNumber.equals(that.departmentNumber) : that.departmentNumber != null)
            return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (ldapGroups != null ? !ldapGroups.equals(that.ldapGroups) : that.ldapGroups != null)
            return false;
        if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
        if (locationNumber != null ? !locationNumber.equals(that.locationNumber) : that.locationNumber != null)
            return false;
        if (locationType != null ? !locationType.equals(that.locationType) : that.locationType != null)
            return false;
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null)
            return false;
        if (thdSsoToken != null ? !thdSsoToken.equals(that.thdSsoToken) : that.thdSsoToken != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userType != null ? !userType.equals(that.userType) : that.userType != null)
            return false;

        return true;
    }

    /*
    Auto-generated hashCode method.
     */
    @Override
    public int hashCode() {
        int result = departmentNumber != null ? departmentNumber.hashCode() : 0;
        result = 31 * result + (empty ? 1 : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (locationNumber != null ? locationNumber.hashCode() : 0);
        result = 31 * result + (locationType != null ? locationType.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        result = 31 * result + imsUserLevel;
        result = 31 * result + (thdSsoToken != null ? thdSsoToken.hashCode() : 0);
        result = 31 * result + (ldapGroups != null ? ldapGroups.hashCode() : 0);
        return result;
    }

    // Methods / statics for Parcelable contract follow below:

    /**
     * CREATOR is part of the Parcelable contract.  This will be used by the Android system to
     * serialize / deserialize session data.
     */
    public static final Creator<SharedUserSessionData> CREATOR =
            new Creator<SharedUserSessionData>() {

                /*
                Fields are read from parcel in same order in which they are defined in this class. A
                new SharedUserSessionData object is
                 */
                @Override
                public SharedUserSessionData createFromParcel(Parcel parcel) {
                    return new SharedUserSessionData(parcel);
                }

                @Override
                public SharedUserSessionData[] newArray(int i) {
                    return new SharedUserSessionData[i];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    /*
    Fields are parceled in the same order in which they're declared in this class.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (empty ? 1 : 0));

        //keep our parcelable payload as small as possible if the session is not available.
        if(!empty) {
            parcel.writeString(departmentNumber);
            parcel.writeString(firstName);
            parcel.writeString(middleName);
            parcel.writeString(lastName);
            parcel.writeString(locationNumber);
            parcel.writeString(locationType);
            parcel.writeString(locale);
            parcel.writeString(userId);
            parcel.writeString(userType);
            parcel.writeInt(imsUserLevel);
            parcel.writeString(thdSsoToken);
            parcel.writeStringList(ldapGroups);
        }
    }
}
