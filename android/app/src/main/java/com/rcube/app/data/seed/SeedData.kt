package com.rcube.app.data.seed

import com.rcube.app.data.model.Booking
import com.rcube.app.data.model.BookingStatus
import com.rcube.app.data.model.CreatorCategory
import com.rcube.app.data.model.CreatorProfile
import com.rcube.app.data.model.EventType
import com.rcube.app.data.model.NotificationItem
import com.rcube.app.data.model.NotificationType
import com.rcube.app.data.model.PayoutStatus
import com.rcube.app.data.model.ProfileStatus
import com.rcube.app.data.model.Service
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * In-memory demo content. Structured to mirror the API shapes in the Bible (§24) so it
 * can be swapped for a real network layer without touching the UI.
 */
object SeedData {

    const val MY_USER_ID = "u_me"
    const val MY_NAME = "Arjun"
    const val MY_PHONE = "+91 98765 43210"

    val myProfiles: List<CreatorProfile> = listOf(
        CreatorProfile(
            id = "p_flute",
            ownerUserId = MY_USER_ID,
            displayName = "Arjun",
            category = CreatorCategory.FLAUTIST,
            bio = "I've played the flute for 8 years. Calm, acoustic sets for intimate " +
                "rooms — cafés, house parties and small gatherings.",
            city = "Bengaluru",
            languages = listOf("English", "Hindi"),
            status = ProfileStatus.APPROVED,
            instagram = "@arjun.flute",
            youtube = "Arjun Flute Sessions",
            completedBookings = 12,
            services = listOf(
                Service("s_f1", "30 Minute Live Performance", 200000, 30,
                    "Acoustic flute set, 6–8 pieces."),
                Service("s_f2", "60 Minute Live Performance", 350000, 60,
                    "Extended set with requests."),
            ),
        ),
        CreatorProfile(
            id = "p_guitar",
            ownerUserId = MY_USER_ID,
            displayName = "Arjun",
            category = CreatorCategory.GUITARIST,
            bio = "Fingerstyle acoustic guitar — mellow covers and originals.",
            city = "Bengaluru",
            languages = listOf("English"),
            status = ProfileStatus.PENDING_REVIEW,
            completedBookings = 0,
            services = listOf(
                Service("s_g1", "60 Minute Acoustic Set", 300000, 60, null),
            ),
        ),
    )

    val directory: List<CreatorProfile> = listOf(
        CreatorProfile(
            id = "d_meera", ownerUserId = "u1", displayName = "Meera",
            category = CreatorCategory.GUITARIST,
            bio = "Acoustic guitarist & vocalist. Soulful covers for warm evenings.",
            city = "Bengaluru", languages = listOf("English", "Kannada"),
            status = ProfileStatus.APPROVED, instagram = "@meera.strings",
            lat = 12.9352, lng = 77.6245, completedBookings = 27,
            services = listOf(
                Service("m1", "30 Minute Live Performance", 250000, 30, null),
                Service("m2", "60 Minute Live Performance", 400000, 60, "Covers + requests."),
            ),
        ),
        CreatorProfile(
            id = "d_sam", ownerUserId = "u2", displayName = "Sam",
            category = CreatorCategory.GUITARIST,
            bio = "Rock & blues guitarist. High energy sets for parties and fests.",
            city = "Bengaluru", languages = listOf("English", "Hindi"),
            status = ProfileStatus.APPROVED, instagram = "@sam.plays",
            lat = 12.9116, lng = 77.6389, completedBookings = 15,
            services = listOf(
                Service("sa1", "45 Minute Set", 300000, 45, null),
                Service("sa2", "90 Minute Set", 550000, 90, null),
            ),
        ),
        CreatorProfile(
            id = "d_priya", ownerUserId = "u3", displayName = "Priya",
            category = CreatorCategory.PHOTOGRAPHER,
            bio = "Candid & portrait photographer. I chase real moments, not poses.",
            city = "Bengaluru", languages = listOf("English"),
            status = ProfileStatus.APPROVED, instagram = "@priya.frames",
            lat = 12.9719, lng = 77.6412, completedBookings = 41,
            services = listOf(
                Service("p1", "Birthday Photoshoot", 500000, 120, "2 hours, edited gallery."),
                Service("p2", "Wedding Photography", 1800000, null, "Full day coverage."),
            ),
        ),
        CreatorProfile(
            id = "d_sneha", ownerUserId = "u4", displayName = "Sneha",
            category = CreatorCategory.MEHENDI_ARTIST,
            bio = "Bridal & party mehendi. Intricate, long-lasting designs.",
            city = "Bengaluru", languages = listOf("Hindi", "English"),
            status = ProfileStatus.APPROVED, instagram = "@sneha.mehendi",
            lat = 12.9250, lng = 77.5938, completedBookings = 33,
            services = listOf(
                Service("sn1", "Party Mehendi", 250000, null, "Up to 5 guests."),
                Service("sn2", "Bridal Mehendi", 700000, null, "Full bridal, both hands & feet."),
            ),
        ),
        CreatorProfile(
            id = "d_kabir", ownerUserId = "u5", displayName = "Kabir",
            category = CreatorCategory.SINGER,
            bio = "Playback-style vocalist. Bollywood, ghazals and unplugged.",
            city = "Bengaluru", languages = listOf("Hindi", "Urdu", "English"),
            status = ProfileStatus.APPROVED, lat = 12.9698, lng = 77.7500, completedBookings = 19,
            services = listOf(
                Service("k1", "Unplugged Evening", 450000, 60, null),
            ),
        ),
        CreatorProfile(
            id = "d_rhea", ownerUserId = "u6", displayName = "Rhea",
            category = CreatorCategory.DANCER,
            bio = "Contemporary & semi-classical performances for stage events.",
            city = "Bengaluru", languages = listOf("English", "Tamil"),
            status = ProfileStatus.APPROVED, instagram = "@rhea.moves",
            lat = 13.0035, lng = 77.5647, completedBookings = 22,
            services = listOf(
                Service("r1", "Solo Performance", 400000, 15, "One choreographed piece."),
                Service("r2", "Event Set", 700000, 40, "Three pieces."),
            ),
        ),
    )

    private val now: LocalDateTime = LocalDateTime.now()
    private val today: LocalDate = LocalDate.now()

    /** Bookings where I am the creator (incoming requests). */
    val creatorBookings: List<Booking> = listOf(
        Booking(
            id = "b1", creatorProfileId = "p_flute", creatorName = MY_NAME,
            creatorCategory = CreatorCategory.FLAUTIST, organizerName = "Priya S.",
            serviceTitle = "60 Minute Live Performance", pricePaise = 350000,
            eventDate = today.plusDays(8), eventType = EventType.HOUSE_PARTY,
            venue = "Koramangala, Bengaluru", notes = "~20 guests, acoustic vibe.",
            status = BookingStatus.PENDING, createdAt = now.minusHours(2),
            acceptExpiresAt = now.plusHours(22),
        ),
        Booking(
            id = "b2", creatorProfileId = "p_flute", creatorName = MY_NAME,
            creatorCategory = CreatorCategory.FLAUTIST, organizerName = "Rohan M.",
            serviceTitle = "30 Minute Live Performance", pricePaise = 200000,
            eventDate = today.plusDays(14), eventType = EventType.CAFE_PERFORMANCE,
            venue = "Indiranagar, Bengaluru", notes = "Weekend brunch set.",
            status = BookingStatus.PAYMENT_PENDING, createdAt = now.minusDays(1),
            paymentExpiresAt = now.plusHours(18),
        ),
        Booking(
            id = "b3", creatorProfileId = "p_flute", creatorName = MY_NAME,
            creatorCategory = CreatorCategory.FLAUTIST, organizerName = "Anaya K.",
            serviceTitle = "30 Minute Live Performance", pricePaise = 200000,
            eventDate = today.plusDays(1), eventType = EventType.BIRTHDAY,
            venue = "HSR Layout, Bengaluru", notes = "Surprise for my dad's 60th.",
            status = BookingStatus.CONFIRMED, createdAt = now.minusDays(3),
            eventOtp = "4291", counterpartyPhone = "+91 90080 12345",
        ),
        Booking(
            id = "b4", creatorProfileId = "p_flute", creatorName = MY_NAME,
            creatorCategory = CreatorCategory.FLAUTIST, organizerName = "Cafe Mocha",
            serviceTitle = "60 Minute Live Performance", pricePaise = 350000,
            eventDate = today.minusDays(3), eventType = EventType.CAFE_PERFORMANCE,
            venue = "Whitefield, Bengaluru", notes = "",
            status = BookingStatus.COMPLETED, createdAt = now.minusDays(10),
            payoutStatus = PayoutStatus.PENDING_TRANSFER, counterpartyPhone = "+91 90080 55555",
        ),
        Booking(
            id = "b5", creatorProfileId = "p_flute", creatorName = MY_NAME,
            creatorCategory = CreatorCategory.FLAUTIST, organizerName = "Isha R.",
            serviceTitle = "30 Minute Live Performance", pricePaise = 200000,
            eventDate = today.minusDays(20), eventType = EventType.HOUSE_PARTY,
            venue = "Jayanagar, Bengaluru", notes = "",
            status = BookingStatus.COMPLETED, createdAt = now.minusDays(25),
            payoutStatus = PayoutStatus.TRANSFERRED,
        ),
    )

    /** Bookings where I am the organizer (outgoing). */
    val organizerBookings: List<Booking> = listOf(
        Booking(
            id = "ob1", creatorProfileId = "d_meera", creatorName = "Meera",
            creatorCategory = CreatorCategory.GUITARIST, organizerName = MY_NAME,
            serviceTitle = "60 Minute Live Performance", pricePaise = 400000,
            eventDate = today.plusDays(12), eventType = EventType.HOUSE_PARTY,
            venue = "Koramangala, Bengaluru", notes = "Anniversary dinner.",
            status = BookingStatus.PENDING, createdAt = now.minusHours(5),
            acceptExpiresAt = now.plusHours(19),
        ),
        Booking(
            id = "ob2", creatorProfileId = "d_sneha", creatorName = "Sneha",
            creatorCategory = CreatorCategory.MEHENDI_ARTIST, organizerName = MY_NAME,
            serviceTitle = "Bridal Mehendi", pricePaise = 700000,
            eventDate = today.plusDays(20), eventType = EventType.WEDDING,
            venue = "Indiranagar, Bengaluru", notes = "Bride + 2 guests.",
            status = BookingStatus.PAYMENT_PENDING, createdAt = now.minusHours(6),
            paymentExpiresAt = now.plusHours(20),
        ),
        Booking(
            id = "ob3", creatorProfileId = "d_priya", creatorName = "Priya",
            creatorCategory = CreatorCategory.PHOTOGRAPHER, organizerName = MY_NAME,
            serviceTitle = "Birthday Photoshoot", pricePaise = 500000,
            eventDate = today.plusDays(2), eventType = EventType.BIRTHDAY,
            venue = "HSR Layout, Bengaluru", notes = "Outdoor, golden hour.",
            status = BookingStatus.CONFIRMED, createdAt = now.minusDays(2),
            eventOtp = "7183", counterpartyPhone = "+91 90080 67890",
        ),
        Booking(
            id = "ob4", creatorProfileId = "d_rhea", creatorName = "Rhea",
            creatorCategory = CreatorCategory.DANCER, organizerName = MY_NAME,
            serviceTitle = "Event Set", pricePaise = 700000,
            eventDate = today.minusDays(6), eventType = EventType.COLLEGE_FEST,
            venue = "MG Road, Bengaluru", notes = "",
            status = BookingStatus.COMPLETED, createdAt = now.minusDays(12),
        ),
    )

    val notifications: List<NotificationItem> = listOf(
        NotificationItem("n1", NotificationType.BOOKING, "New booking request \uD83C\uDFB5",
            "Priya S. wants to book your 60 Minute Live Performance.", "2h ago", false),
        NotificationItem("n2", NotificationType.PAYMENT, "Accepted — pay to confirm",
            "Sneha accepted your Bridal Mehendi request.", "6h ago", false),
        NotificationItem("n3", NotificationType.PROFILE, "You're verified! \u2713",
            "Your Flautist profile is live. Time to get recognized.", "2d ago", true),
        NotificationItem("n4", NotificationType.PAYOUT, "You've been paid \u2705",
            "\u20B91,700 has been transferred to you for a 30 Minute set.", "20d ago", true),
    )
}
