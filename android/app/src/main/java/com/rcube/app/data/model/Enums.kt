package com.rcube.app.data.model

/** Which experience the single account is currently using (Bible §9, FR-AUTH-4). */
enum class Mode { CREATOR, ORGANIZER }

enum class CreatorCategory(val label: String, val emoji: String) {
    SINGER("Singer", "\uD83C\uDFA4"),
    GUITARIST("Guitarist", "\uD83C\uDFB8"),
    FLAUTIST("Flautist", "\uD83C\uDFB5"),
    MUSICIAN("Musician", "\uD83C\uDFB9"),
    DANCER("Dancer", "\uD83D\uDC83"),
    PHOTOGRAPHER("Photographer", "\uD83D\uDCF7"),
    MEHENDI_ARTIST("Mehendi Artist", "\uD83C\uDF3F"),
    STORYTELLER("Storyteller", "\uD83D\uDCD6"),
    PAINTER("Painter", "\uD83C\uDFA8"),
    MAGICIAN("Magician", "\u2728"),
    CALLIGRAPHER("Calligrapher", "\u270D\uFE0F"),
    OTHER("Other", "\uD83C\uDFAD"),
}

enum class EventType(val label: String, val emoji: String) {
    BIRTHDAY("Birthday", "\uD83C\uDF82"),
    WEDDING("Wedding", "\uD83D\uDC8D"),
    HOUSE_PARTY("House Party", "\uD83C\uDFE0"),
    CORPORATE_EVENT("Corporate", "\uD83D\uDCBC"),
    CAFE_PERFORMANCE("Café", "\u2615"),
    COLLEGE_FEST("College Fest", "\uD83C\uDF93"),
    RELIGIOUS_EVENT("Religious", "\uD83D\uDD4A\uFE0F"),
    COMMUNITY_EVENT("Community", "\uD83E\uDD1D"),
    OTHER("Other", "\uD83C\uDF89"),
}

/** Creator profile lifecycle (skill/socials review, separate from Aadhaar identity). */
enum class ProfileStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_REVIEW("Pending Review"),
    APPROVED("Approved"),
    REJECTED("Changes requested"),
    SUSPENDED("Suspended"),
}

/** A portfolio media item supporting a creator's skill claim. */
enum class MediaType { IMAGE, VIDEO }

/** Account-level identity (Aadhaar) verification status. */
enum class AadhaarStatus(val label: String) {
    NOT_SUBMITTED("Not submitted"),
    PENDING_REVIEW("Under review"),
    VERIFIED("Verified"),
    REJECTED("Needs changes");

    val isVerified: Boolean get() = this == VERIFIED
}

enum class BookingGroup { PENDING, ACTIVE, DONE }

/** Core booking state machine (Bible §19.1). */
enum class BookingStatus(val label: String, val group: BookingGroup) {
    PENDING("Pending", BookingGroup.PENDING),
    ACCEPTED("Accepted", BookingGroup.PENDING),
    PAYMENT_PENDING("Payment Pending", BookingGroup.PENDING),
    CONFIRMED("Confirmed", BookingGroup.ACTIVE),
    IN_PROGRESS("In Progress", BookingGroup.ACTIVE),
    COMPLETED("Completed", BookingGroup.DONE),
    DECLINED("Declined", BookingGroup.DONE),
    EXPIRED("Expired", BookingGroup.DONE),
    PAYMENT_EXPIRED("Payment Expired", BookingGroup.DONE),
    CANCELLED("Cancelled", BookingGroup.DONE);

    /** Contact is revealed only from CONFIRMED onward (Bible BR-18). */
    val contactRevealed: Boolean
        get() = this == CONFIRMED || this == IN_PROGRESS || this == COMPLETED
}

/** Payout lifecycle (Bible §19.3). */
enum class PayoutStatus(val label: String) {
    PENDING_TRANSFER("Pending Transfer"),
    TRANSFER_INITIATED("Transfer Initiated"),
    TRANSFERRED("Transferred"),
    FAILED("Failed"),
}

enum class NotificationType { BOOKING, PAYMENT, PROFILE, PAYOUT, REMINDER }
