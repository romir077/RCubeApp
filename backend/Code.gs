/**
 * RCube MVP backend — Google Apps Script + Google Sheets.
 *
 * The whole API is a single Web App endpoint. Requests are RPC-style:
 *   POST { "action": "...", "apiKey": "...", "token": "...", "data": { ... } }
 * Response envelope:
 *   { "ok": true, "data": <result> }   or   { "ok": false, "error": "message" }
 *
 * SETUP (see BACKEND_SETUP.md for the full walkthrough):
 *   1. Create a Google Sheet, then Extensions > Apps Script (this makes the script
 *      "container-bound" so getActiveSpreadsheet() works).
 *   2. Paste this file, set API_KEY below to your own random string.
 *   3. Run the `setup` function once (creates tabs + seeds demo creators).
 *   4. Deploy > New deployment > Web app > Execute as: Me, Who has access: Anyone.
 *   5. Copy the /exec URL + API_KEY into android/local.properties.
 */

// ------------------------------------------------------------------ CONFIG ----

var API_KEY = 'REPLACE_WITH_YOUR_RCUBE_API_KEY'; // must match RCUBE_API_KEY in the app
var DEV_MODE = true;            // MVP: accept any 4-6 digit OTP (no SMS provider yet)
var COMMISSION_PCT = 15;        // platform commission (Bible BR-20)
var ACCEPT_WINDOW_HOURS = 24;   // pending request expiry
var PAYMENT_WINDOW_HOURS = 24;  // accepted -> pay expiry
var SPREADSHEET_ID = '';        // only needed if the script is NOT bound to the sheet

// -------------------------------------------------------------- DATA MODEL ----

var SHEETS = {
  users: ['id', 'phone', 'firstName', 'lastName', 'displayName', 'defaultMode',
    'aadhaarStatus', 'aadhaarFront', 'aadhaarBack', 'profilePhoto', 'createdAt'],
  sessions: ['token', 'userId', 'createdAt'],
  creator_profiles: ['id', 'userId', 'displayName', 'category', 'bio', 'city',
    'languages', 'status', 'active', 'instagram', 'youtube', 'completedBookings',
    'rejectionReason', 'lat', 'lng', 'distanceKm',
    'profilePhoto', 'media1', 'media2', 'media3', 'createdAt'],
  services: ['id', 'profileId', 'title', 'pricePaise', 'durationMinutes',
    'description', 'isActive', 'createdAt'],
  reviews: ['id', 'bookingId', 'profileId', 'organizerName', 'rating',
    'comment', 'createdAt'],
  bookings: ['id', 'organizerUserId', 'organizerName', 'organizerPhone',
    'creatorProfileId', 'creatorUserId', 'creatorName', 'creatorCategory',
    'creatorPhone', 'serviceId', 'serviceTitle', 'pricePaise', 'eventDate',
    'eventType', 'venue', 'notes', 'status', 'acceptExpiresAt',
    'paymentExpiresAt', 'eventOtp', 'payoutStatus', 'commissionPct', 'createdAt'],
  notifications: ['id', 'userId', 'type', 'title', 'body', 'read', 'createdAt']
};

var MUTATIONS = {
  authVerify: true, setName: true, submitAadhaar: true, setUserPhoto: true,
  createProfile: true, updateProfile: true, setProfilePhoto: true,
  setProfileActive: true, deleteProfile: true,
  addPortfolioMedia: true, deletePortfolioMedia: true,
  addService: true, updateService: true, deleteService: true, submitProfile: true,
  createBooking: true, acceptRequest: true, declineRequest: true,
  startEventWithOtp: true, payAndConfirm: true, completeBooking: true,
  cancelBooking: true, markNotificationsRead: true
};

// ---------------------------------------------------------------- ROUTING ----

function doGet(e) { return handle_(e); }
function doPost(e) { return handle_(e); }

function handle_(e) {
  var lock = null;
  try {
    var body = {};
    if (e && e.postData && e.postData.contents) {
      body = JSON.parse(e.postData.contents);
    }
    var params = (e && e.parameter) ? e.parameter : {};
    var action = body.action || params.action || 'ping';
    var apiKey = body.apiKey || params.apiKey || '';
    var token = body.token || params.token || '';
    var data = body.data || {};

    if (action !== 'ping' && apiKey !== API_KEY) {
      return json_({ ok: false, error: 'unauthorized' });
    }

    // Serialize writes to avoid concurrent-write corruption in Sheets.
    if (MUTATIONS[action]) {
      lock = LockService.getScriptLock();
      lock.waitLock(30000);
    }

    var result = route_(action, token, data);
    return json_({ ok: true, data: result });
  } catch (err) {
    return json_({ ok: false, error: (err && err.message) ? err.message : String(err) });
  } finally {
    if (lock) lock.releaseLock();
  }
}

function route_(action, token, data) {
  switch (action) {
    case 'ping': return { pong: true, time: Date.now() };
    case 'authVerify': return authVerify_(data);
    case 'setName': return setName_(requireUser_(token), data);
    case 'submitAadhaar': return submitAadhaar_(requireUser_(token), data);
    case 'setUserPhoto': return setUserPhoto_(requireUser_(token), data);
    case 'getState': return getState_(requireUser_(token));
    case 'searchCreators': return searchCreators_(requireUser_(token), data);
    case 'getCreatorPublic': return getCreatorPublic_(data);
    case 'createProfile': return createProfile_(requireUser_(token), data);
    case 'updateProfile': return updateProfile_(requireUser_(token), data);
    case 'setProfilePhoto': return setProfilePhoto_(requireUser_(token), data);
    case 'setProfileActive': return setProfileActive_(requireUser_(token), data);
    case 'deleteProfile': return deleteProfile_(requireUser_(token), data);
    case 'addPortfolioMedia': return addPortfolioMedia_(requireUser_(token), data);
    case 'deletePortfolioMedia': return deletePortfolioMedia_(requireUser_(token), data);
    case 'addService': return addService_(requireUser_(token), data);
    case 'updateService': return updateService_(requireUser_(token), data);
    case 'deleteService': return deleteService_(requireUser_(token), data);
    case 'submitProfile': return submitProfile_(requireUser_(token), data);
    case 'createBooking': return createBooking_(requireUser_(token), data);
    case 'acceptRequest': return setCreatorBookingStatus_(requireUser_(token), data.bookingId, 'accept');
    case 'declineRequest': return setCreatorBookingStatus_(requireUser_(token), data.bookingId, 'decline');
    case 'startEventWithOtp': return startEventWithOtp_(requireUser_(token), data);
    case 'payAndConfirm': return payAndConfirm_(requireUser_(token), data);
    case 'completeBooking': return completeBooking_(requireUser_(token), data);
    case 'cancelBooking': return cancelBooking_(requireUser_(token), data);
    case 'markNotificationsRead': return markNotificationsRead_(requireUser_(token));
    default: throw new Error('unknown action: ' + action);
  }
}

// ------------------------------------------------------------------- AUTH ----

function authVerify_(data) {
  var phone = String(data.phone || '').trim();
  var otp = String(data.otp || '').trim();
  if (!phone) throw new Error('phone required');
  if (!DEV_MODE && otp.length < 4) throw new Error('invalid otp');

  var user = findRow_('users', 'phone', phone);
  if (!user) {
    user = {
      id: 'u_' + shortId_(),
      phone: phone,
      firstName: '', lastName: '', displayName: '',
      defaultMode: 'creator',
      aadhaarStatus: 'NOT_SUBMITTED', aadhaarFront: '', aadhaarBack: '',
      createdAt: Date.now()
    };
    appendObj_('users', user);
  }
  var token = 'tok_' + Utilities.getUuid().replace(/-/g, '');
  appendObj_('sessions', { token: token, userId: user.id, createdAt: Date.now() });

  return { token: token, user: userDto_(user) };
}

function userDto_(u) {
  return {
    id: u.id, phone: u.phone,
    displayName: u.displayName || '',
    firstName: u.firstName || '',
    lastName: u.lastName || '',
    defaultMode: u.defaultMode || 'creator',
    aadhaarStatus: u.aadhaarStatus || 'NOT_SUBMITTED',
    profilePhotoUrl: mediaThumbUrl_(u.profilePhoto)
  };
}

function setUserPhoto_(user, data) {
  var url = driveSavePublicUrl_('RCube Photos (public)',
    user.id + '_userphoto_' + Date.now() + '.jpg', 'image/jpeg', data.base64);
  var row = findRow_('users', 'id', user.id);
  updateRow_('users', row._row, { profilePhoto: url });
  return userDto_(findRow_('users', 'id', user.id));
}

function setName_(user, data) {
  if (String(user.aadhaarStatus) === 'VERIFIED') throw new Error('name_locked');
  var first = String(data.firstName || '').trim();
  var last = String(data.lastName || '').trim();
  var row = findRow_('users', 'id', user.id);
  updateRow_('users', row._row, {
    firstName: first, lastName: last, displayName: (first + ' ' + last).trim()
  });
  return userDto_(findRow_('users', 'id', user.id));
}

function submitAadhaar_(user, data) {
  data = data || {};
  var row = findRow_('users', 'id', user.id);
  var frontUrl = saveAadhaarImage_(user.id, 'front', data.frontBase64) || row.aadhaarFront || 'uploaded';
  var backUrl = saveAadhaarImage_(user.id, 'back', data.backBase64) || row.aadhaarBack || 'uploaded';
  updateRow_('users', row._row, {
    aadhaarStatus: 'PENDING_REVIEW', aadhaarFront: frontUrl, aadhaarBack: backUrl
  });
  return userDto_(findRow_('users', 'id', user.id));
}

/** Decode a base64 JPEG into a private Drive folder; returns the file URL (or '' if none). */
function saveAadhaarImage_(userId, side, base64) {
  if (!base64) return '';
  var clean = base64.indexOf(',') >= 0 ? base64.substring(base64.indexOf(',') + 1) : base64;
  var bytes = Utilities.base64Decode(clean);
  var name = userId + '_aadhaar_' + side + '_' + Date.now() + '.jpg';
  var blob = Utilities.newBlob(bytes, 'image/jpeg', name);
  var file = aadhaarFolder_().createFile(blob);
  return file.getUrl();
}

function aadhaarFolder_() {
  var folderName = 'RCube Aadhaar (private)';
  var it = DriveApp.getFoldersByName(folderName);
  return it.hasNext() ? it.next() : DriveApp.createFolder(folderName);
}

function requireUser_(token) {
  if (!token) throw new Error('unauthorized');
  var session = findRow_('sessions', 'token', token);
  if (!session) throw new Error('unauthorized');
  var user = findRow_('users', 'id', session.userId);
  if (!user) throw new Error('unauthorized');
  return user;
}

// ------------------------------------------------------------------ STATE ----

function getState_(user) {
  var profiles = rows_('creator_profiles').filter(function (p) {
    return p.userId === user.id && p.status !== 'DELETED';
  });
  var services = rows_('services');
  var bookings = rows_('bookings');
  var notifs = rows_('notifications').filter(function (n) { return n.userId === user.id; });

  return {
    me: userDto_(user),
    myProfiles: profiles.map(function (p) { return profileDto_(p, services); }),
    creatorBookings: bookings
      .filter(function (b) { return b.creatorUserId === user.id; })
      .map(function (b) { return bookingDto_(b, 'creator'); }),
    organizerBookings: bookings
      .filter(function (b) { return b.organizerUserId === user.id; })
      .map(function (b) { return bookingDto_(b, 'organizer'); }),
    notifications: notifs.map(notificationDto_)
  };
}

// -------------------------------------------------------------- DISCOVERY ----

function searchCreators_(user, data) {
  if (String(user.aadhaarStatus) !== 'VERIFIED') throw new Error('IDENTITY_NOT_VERIFIED');
  var category = data.category || null;
  var radius = Number(data.radiusKm || 9999);
  var oLat = (data.lat === undefined || data.lat === null || data.lat === '') ? null : Number(data.lat);
  var oLng = (data.lng === undefined || data.lng === null || data.lng === '') ? null : Number(data.lng);
  var services = rows_('services');
  var out = [];

  rows_('creator_profiles').forEach(function (p) {
    if (p.status !== 'APPROVED') return;
    if (String(p.active) === 'false') return;   // deactivated by the creator
    if (category && p.category !== category) return;
    var dto = profileDto_(p, services);
    if (!dto.ownerVerified) return;             // only verified creators are discoverable
    if (dto.lat != null && dto.lng != null && oLat != null && oLng != null) {
      var d = haversineKm_(oLat, oLng, dto.lat, dto.lng);
      if (d > radius) return;                 // outside the requested radius
      dto.distanceKm = Math.round(d * 10) / 10;
    } else {
      dto.distanceKm = null;                  // unknown location -> keep, no radius filter
    }
    out.push(dto);
  });

  out.sort(function (a, b) {
    var da = a.distanceKm == null ? 1e9 : a.distanceKm;
    var db = b.distanceKm == null ? 1e9 : b.distanceKm;
    return da - db;
  });
  return out;
}

function haversineKm_(lat1, lng1, lat2, lng2) {
  var R = 6371;
  var dLat = toRad_(lat2 - lat1);
  var dLng = toRad_(lng2 - lng1);
  var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad_(lat1)) * Math.cos(toRad_(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}
function toRad_(d) { return d * Math.PI / 180; }

function getCreatorPublic_(data) {
  var p = findRow_('creator_profiles', 'id', data.profileId);
  if (!p) throw new Error('profile not found');
  return profileDto_(p, rows_('services'));
}

// ------------------------------------------------------ PROFILES & SERVICES ----

function createProfile_(user, data) {
  var id = 'p_' + shortId_();
  appendObj_('creator_profiles', {
    id: id, userId: user.id, displayName: user.displayName,
    category: data.category, bio: data.bio || '', city: data.city || '',
    languages: (data.languages || []).join(','), status: 'DRAFT', active: true,
    instagram: data.instagram || '', youtube: data.youtube || '',
    completedBookings: 0, rejectionReason: '',
    lat: (data.lat === undefined || data.lat === null) ? '' : data.lat,
    lng: (data.lng === undefined || data.lng === null) ? '' : data.lng,
    distanceKm: '', createdAt: Date.now()
  });
  // Services added during profile creation are persisted immediately.
  (data.services || []).forEach(function (s) {
    appendObj_('services', {
      id: 's_' + shortId_(), profileId: id, title: s.title,
      pricePaise: Number(s.pricePaise), durationMinutes: s.durationMinutes || '',
      description: s.description || '', isActive: true, createdAt: Date.now()
    });
  });
  return profileDto_(findRow_('creator_profiles', 'id', id), rows_('services'));
}

function addService_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  var id = 's_' + shortId_();
  appendObj_('services', {
    id: id, profileId: profile.id, title: data.title,
    pricePaise: Number(data.pricePaise), durationMinutes: data.durationMinutes || '',
    description: data.description || '', isActive: true, createdAt: Date.now()
  });
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

function updateService_(user, data) {
  ownedProfile_(user, data.profileId);
  var svc = findRow_('services', 'id', data.serviceId);
  if (!svc || String(svc.profileId) !== String(data.profileId)) throw new Error('service not found');
  updateRow_('services', svc._row, {
    title: data.title, pricePaise: Number(data.pricePaise),
    durationMinutes: data.durationMinutes || '', description: data.description || ''
  });
  return profileDto_(findRow_('creator_profiles', 'id', data.profileId), rows_('services'));
}

function deleteService_(user, data) {
  ownedProfile_(user, data.profileId);
  var svc = findRow_('services', 'id', data.serviceId);
  if (svc) updateRow_('services', svc._row, { isActive: false });
  return profileDto_(findRow_('creator_profiles', 'id', data.profileId), rows_('services'));
}

function updateProfile_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  // Bio / city / languages are editable anytime (even after approval).
  var patch = {
    bio: data.bio || '', city: data.city || '', languages: (data.languages || []).join(',')
  };
  // Category is locked once the profile is approved.
  if (profile.status === 'DRAFT' || profile.status === 'REJECTED') {
    patch.category = data.category || profile.category;
  }
  // A present handle locks once approved; a missing one can still be added.
  var igLocked = profile.status === 'APPROVED' && String(profile.instagram || '') !== '';
  var ytLocked = profile.status === 'APPROVED' && String(profile.youtube || '') !== '';
  if (!igLocked) patch.instagram = data.instagram || '';
  if (!ytLocked) patch.youtube = data.youtube || '';
  updateRow_('creator_profiles', profile._row, patch);
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

function setProfilePhoto_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  var url = driveSavePublicUrl_('RCube Photos (public)',
    profile.id + '_photo_' + Date.now() + '.jpg', 'image/jpeg', data.base64);
  updateRow_('creator_profiles', profile._row, { profilePhoto: url });
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

function setProfileActive_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  updateRow_('creator_profiles', profile._row, { active: (data.active === false ? false : true) });
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

/** Soft-delete a draft/rejected profile (keeps the row but hides it everywhere). */
function deleteProfile_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  if (profile.status === 'APPROVED' || profile.status === 'PENDING_REVIEW') {
    throw new Error('cannot_delete_live');
  }
  updateRow_('creator_profiles', profile._row, { status: 'DELETED', active: false });
  return { ok: true, deletedId: profile.id };
}

function addPortfolioMedia_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  var type = (data.type === 'VIDEO') ? 'VIDEO' : 'IMAGE';
  var slotCol = null;
  ['media1', 'media2', 'media3'].forEach(function (c) {
    if (!slotCol && String(profile[c] || '') === '') slotCol = c;
  });
  if (!slotCol) throw new Error('max_portfolio');
  var ext = (type === 'VIDEO') ? 'mp4' : 'jpg';
  var mime = (type === 'VIDEO') ? 'video/mp4' : 'image/jpeg';
  var url = driveSavePublicUrl_('RCube Portfolio (public)',
    profile.id + '_' + slotCol + '_' + Date.now() + '.' + ext, mime, data.base64);
  var patch = {};
  patch[slotCol] = type + '|' + url;   // store a real, human-clickable URL (like Aadhaar)
  updateRow_('creator_profiles', profile._row, patch);
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

function deletePortfolioMedia_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  var col = 'media' + Number(data.slot);
  if (['media1', 'media2', 'media3'].indexOf(col) < 0) throw new Error('bad_slot');
  var patch = {};
  patch[col] = '';
  updateRow_('creator_profiles', profile._row, patch);
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

/** Create a PUBLIC (anyone-with-link) Drive file and return its shareable file URL. */
function driveSavePublicUrl_(folderName, name, mime, base64) {
  var clean = base64.indexOf(',') >= 0 ? base64.substring(base64.indexOf(',') + 1) : base64;
  var bytes = Utilities.base64Decode(clean);
  var blob = Utilities.newBlob(bytes, mime, name);
  var file = folderNamed_(folderName).createFile(blob);
  try {
    file.setSharing(DriveApp.Access.ANYONE_WITH_LINK, DriveApp.Permission.VIEW);
  } catch (e) { /* sharing may be restricted by domain policy */ }
  return driveFileView_(file.getId());
}

function folderNamed_(name) {
  var it = DriveApp.getFoldersByName(name);
  return it.hasNext() ? it.next() : DriveApp.createFolder(name);
}

/** Pull the Drive file id out of a stored value (a URL or a bare id). */
function extractDriveId_(s) {
  s = String(s || '');
  var m = s.match(/[-\w]{25,}/);
  return m ? m[0] : s;
}

function driveThumb_(v) {
  var id = extractDriveId_(v);
  return id ? 'https://drive.google.com/thumbnail?id=' + id + '&sz=w1200' : null;
}
function driveImage_(id) { return 'https://drive.google.com/uc?export=view&id=' + extractDriveId_(id); }
function driveStream_(id) { return 'https://drive.google.com/uc?export=download&id=' + extractDriveId_(id); }
function driveFileView_(id) { return 'https://drive.google.com/file/d/' + extractDriveId_(id) + '/view'; }

/** Thumbnail URL for a stored media value (URL or id), or null if empty. */
function mediaThumbUrl_(v) { return String(v || '') ? driveThumb_(v) : null; }

function submitProfile_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  updateRow_('creator_profiles', profile._row, { status: 'PENDING_REVIEW' });
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

// ---------------------------------------------------------------- BOOKINGS ----

function createBooking_(user, data) {
  var profile = findRow_('creator_profiles', 'id', data.creatorProfileId);
  if (!profile) throw new Error('creator not found');
  var service = findRow_('services', 'id', data.serviceId);
  if (!service) throw new Error('service not found');
  var creatorUser = findRow_('users', 'id', profile.userId);

  if (String(user.aadhaarStatus) !== 'VERIFIED') throw new Error('IDENTITY_NOT_VERIFIED');
  if (!creatorUser || String(creatorUser.aadhaarStatus) !== 'VERIFIED') {
    throw new Error('creator_not_verified');
  }

  var id = 'ob_' + shortId_();
  appendObj_('bookings', {
    id: id,
    organizerUserId: user.id, organizerName: user.displayName, organizerPhone: user.phone,
    creatorProfileId: profile.id, creatorUserId: profile.userId,
    creatorName: profile.displayName, creatorCategory: profile.category,
    creatorPhone: creatorUser ? creatorUser.phone : '',
    serviceId: service.id, serviceTitle: service.title, pricePaise: Number(service.pricePaise),
    eventDate: data.eventDate, eventType: data.eventType, venue: data.venue || '',
    notes: data.notes || '', status: 'PENDING',
    acceptExpiresAt: Date.now() + ACCEPT_WINDOW_HOURS * 3600000,
    paymentExpiresAt: '', eventOtp: '', payoutStatus: '',
    commissionPct: COMMISSION_PCT, createdAt: Date.now()
  });
  addNotification_(profile.userId, 'BOOKING', 'New booking request',
    user.displayName + ' wants to book your ' + service.title + '.');
  return bookingDto_(findRow_('bookings', 'id', id), 'organizer');
}

function setCreatorBookingStatus_(user, bookingId, kind) {
  var b = findRow_('bookings', 'id', bookingId);
  if (!b) throw new Error('booking not found');
  if (b.creatorUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'PENDING') throw new Error('booking not pending');

  if (kind === 'accept') {
    updateRow_('bookings', b._row, {
      status: 'PAYMENT_PENDING',
      paymentExpiresAt: Date.now() + PAYMENT_WINDOW_HOURS * 3600000
    });
    addNotification_(b.organizerUserId, 'PAYMENT', 'Accepted — pay to confirm',
      b.creatorName + ' accepted your ' + b.serviceTitle + ' request.');
  } else {
    updateRow_('bookings', b._row, { status: 'DECLINED' });
  }
  return bookingDto_(findRow_('bookings', 'id', bookingId), 'creator');
}

function payAndConfirm_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.organizerUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'PAYMENT_PENDING') throw new Error('not payable');

  var otp = String(Math.floor(1000 + Math.random() * 9000));
  updateRow_('bookings', b._row, { status: 'CONFIRMED', eventOtp: otp });
  addNotification_(b.creatorUserId, 'BOOKING', "It's confirmed!",
    'Your ' + b.serviceTitle + ' on ' + b.eventDate + ' is confirmed. Contact shared.');
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'organizer');
}

function startEventWithOtp_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.creatorUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'CONFIRMED') throw new Error('not confirmed');
  if (String(b.eventOtp) !== String(data.otp).trim()) throw new Error('OTP_INVALID');

  updateRow_('bookings', b._row, { status: 'IN_PROGRESS' });
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'creator');
}

function completeBooking_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.organizerUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'IN_PROGRESS' && b.status !== 'CONFIRMED') throw new Error('not completable');

  updateRow_('bookings', b._row, { status: 'COMPLETED', payoutStatus: 'PENDING_TRANSFER' });
  bumpCompletedCount_(b.creatorProfileId);

  // Optional organizer rating + comment, shown on the creator's profile.
  var rating = Number(data.rating) || 0;
  if (rating > 0 && !findReview_(b.id)) {
    appendObj_('reviews', {
      id: 'rev_' + shortId_(), bookingId: b.id, profileId: b.creatorProfileId,
      organizerName: b.organizerName || 'Organizer',
      rating: Math.max(1, Math.min(5, Math.round(rating))),
      comment: String(data.comment || '').slice(0, 600), createdAt: Date.now()
    });
  }

  addNotification_(b.creatorUserId, 'PAYOUT', 'Completed — earning on the way',
    'Your ' + b.serviceTitle + ' is complete. Payout is pending transfer.');
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'organizer');
}

function findReview_(bookingId) {
  return rows_('reviews').filter(function (r) { return r.bookingId === bookingId; })[0] || null;
}

function cancelBooking_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.organizerUserId !== user.id) throw new Error('forbidden');
  updateRow_('bookings', b._row, { status: 'CANCELLED' });
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'organizer');
}

// ----------------------------------------------------------- NOTIFICATIONS ----

function markNotificationsRead_(user) {
  var sh = sheet_('notifications');
  var all = rows_('notifications');
  all.forEach(function (n) {
    if (n.userId === user.id && n.read !== true) {
      updateRow_('notifications', n._row, { read: true });
    }
  });
  return { ok: true };
}

function addNotification_(userId, type, title, body) {
  appendObj_('notifications', {
    id: 'n_' + shortId_(), userId: userId, type: type,
    title: title, body: body, read: false, createdAt: Date.now()
  });
}

// ------------------------------------------------------------------- DTOS ----

function profileDto_(p, services) {
  var svc = services.filter(function (s) {
    return s.profileId === p.id && s.isActive !== false;
  }).map(serviceDto_);

  var portfolio = [];
  [1, 2, 3].forEach(function (n) {
    var raw = String(p['media' + n] || '');
    if (raw) {
      var sep = raw.indexOf('|');
      var type = (sep >= 0 ? raw.substring(0, sep) : 'IMAGE') || 'IMAGE';
      var stored = sep >= 0 ? raw.substring(sep + 1) : raw;  // a URL (or legacy id)
      portfolio.push({
        slot: n, type: type,
        thumbUrl: driveThumb_(stored),
        fullUrl: (type === 'VIDEO') ? driveStream_(stored) : driveImage_(stored)
      });
    }
  });

  var reviews = rows_('reviews').filter(function (r) { return r.profileId === p.id; });
  var ratingSum = 0;
  var reviewDtos = reviews.map(function (r) {
    ratingSum += Number(r.rating) || 0;
    return {
      organizerName: r.organizerName || 'Organizer',
      rating: Number(r.rating) || 0,
      comment: r.comment || '',
      createdAt: Number(r.createdAt) || 0
    };
  }).sort(function (a, b) { return b.createdAt - a.createdAt; });
  var ratingCount = reviewDtos.length;
  var ratingAvg = ratingCount ? Math.round((ratingSum / ratingCount) * 10) / 10 : null;

  return {
    id: p.id, ownerUserId: p.userId, displayName: p.displayName,
    category: p.category, bio: p.bio || '', city: p.city || '',
    languages: p.languages ? String(p.languages).split(',').map(trim_).filter(nonEmpty_) : [],
    status: p.status, active: String(p.active) === 'false' ? false : true,
    services: svc, portfolio: portfolio,
    profilePhotoUrl: mediaThumbUrl_(p.profilePhoto),
    instagram: p.instagram || null, youtube: p.youtube || null,
    rejectionReason: p.rejectionReason || null,
    lat: (p.lat === '' || p.lat === null) ? null : Number(p.lat),
    lng: (p.lng === '' || p.lng === null) ? null : Number(p.lng),
    distanceKm: (p.distanceKm === '' || p.distanceKm === null) ? null : Number(p.distanceKm),
    completedBookings: Number(p.completedBookings) || 0,
    ownerVerified: isOwnerVerified_(p.userId),
    ratingAvg: ratingAvg, ratingCount: ratingCount, reviews: reviewDtos
  };
}

function serviceDto_(s) {
  return {
    id: s.id, title: s.title, pricePaise: Number(s.pricePaise),
    durationMinutes: (s.durationMinutes === '' || s.durationMinutes === null) ? null : Number(s.durationMinutes),
    description: s.description || null
  };
}

function bookingDto_(b, perspective) {
  var revealed = (b.status === 'CONFIRMED' || b.status === 'IN_PROGRESS' || b.status === 'COMPLETED');
  var counterpartyPhone = null;
  if (revealed) counterpartyPhone = (perspective === 'creator') ? b.organizerPhone : b.creatorPhone;
  var eventOtp = (perspective === 'organizer' && b.eventOtp) ? String(b.eventOtp) : null;
  return {
    id: b.id, creatorProfileId: b.creatorProfileId, creatorName: b.creatorName,
    creatorCategory: b.creatorCategory, organizerName: b.organizerName,
    serviceTitle: b.serviceTitle, pricePaise: Number(b.pricePaise),
    eventDate: dateStr_(b.eventDate), eventType: b.eventType,
    venue: b.venue || '', notes: b.notes || '', status: b.status,
    createdAt: Number(b.createdAt) || Date.now(),
    acceptExpiresAt: b.acceptExpiresAt ? Number(b.acceptExpiresAt) : null,
    paymentExpiresAt: b.paymentExpiresAt ? Number(b.paymentExpiresAt) : null,
    eventOtp: eventOtp, counterpartyPhone: counterpartyPhone || null,
    payoutStatus: b.payoutStatus || null,
    commissionPct: Number(b.commissionPct) || COMMISSION_PCT
  };
}

function notificationDto_(n) {
  return {
    id: n.id, type: n.type, title: n.title, body: n.body,
    read: n.read === true, createdAt: Number(n.createdAt) || Date.now()
  };
}

// ---------------------------------------------------------- SHEET HELPERS ----

function ss_() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  if (ss) return ss;
  if (SPREADSHEET_ID) return SpreadsheetApp.openById(SPREADSHEET_ID);
  throw new Error('No spreadsheet bound. Create the script from the Sheet (Extensions > Apps Script) or set SPREADSHEET_ID.');
}

function sheet_(name) {
  var ss = ss_();
  var sh = ss.getSheetByName(name);
  if (!sh) {
    sh = ss.insertSheet(name);
    sh.appendRow(SHEETS[name]);
  }
  return sh;
}

function rows_(name) {
  var sh = sheet_(name);
  var values = sh.getDataRange().getValues();
  if (values.length < 2) return [];
  var headers = values[0];
  var out = [];
  for (var i = 1; i < values.length; i++) {
    var obj = {};
    for (var j = 0; j < headers.length; j++) obj[headers[j]] = values[i][j];
    obj._row = i + 1;
    out.push(obj);
  }
  return out;
}

function findRow_(name, field, value) {
  var all = rows_(name);
  for (var i = 0; i < all.length; i++) {
    if (String(all[i][field]) === String(value)) return all[i];
  }
  return null;
}

function appendObj_(name, obj) {
  var sh = sheet_(name);
  var headers = SHEETS[name];
  var row = headers.map(function (h) {
    var val = (obj[h] === undefined || obj[h] === null) ? '' : obj[h];
    return safe_(val);
  });
  sh.appendRow(row);
}

function updateRow_(name, rowIndex, patch) {
  var sh = sheet_(name);
  var headers = SHEETS[name];
  for (var j = 0; j < headers.length; j++) {
    if (patch.hasOwnProperty(headers[j])) {
      var v = patch[headers[j]];
      sh.getRange(rowIndex, j + 1).setValue(v === null ? '' : safe_(v));
    }
  }
}

function ownedProfile_(user, profileId) {
  var p = findRow_('creator_profiles', 'id', profileId);
  if (!p) throw new Error('profile not found');
  if (p.userId !== user.id) throw new Error('forbidden');
  return p;
}

function bumpCompletedCount_(profileId) {
  var p = findRow_('creator_profiles', 'id', profileId);
  if (p) updateRow_('creator_profiles', p._row, { completedBookings: (Number(p.completedBookings) || 0) + 1 });
}

function isOwnerVerified_(userId) {
  var u = findRow_('users', 'id', userId);
  return !!(u && String(u.aadhaarStatus) === 'VERIFIED');
}

// ----------------------------------------------------------------- UTILS ----

function json_(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}
function shortId_() { return Utilities.getUuid().replace(/-/g, '').slice(0, 8); }

/**
 * Prevent Google Sheets from treating a value as a formula. A leading +, =, - or @
 * (e.g. phone numbers like "+91 98765...") would otherwise show up as #ERROR!.
 * The apostrophe forces text; getValue() reads the value back without it.
 */
function safe_(v) {
  if (typeof v === 'string' && v.length > 0 && '=+-@'.indexOf(v.charAt(0)) !== -1) {
    return "'" + v;
  }
  return v;
}

function trim_(s) { return String(s).trim(); }
function nonEmpty_(s) { return s && s.length > 0; }
function dateStr_(v) {
  if (v instanceof Date) {
    return Utilities.formatDate(v, Session.getScriptTimeZone() || 'Asia/Kolkata', 'yyyy-MM-dd');
  }
  return String(v);
}

// ------------------------------------------------------ SETUP / SEED (once) ----

/** Run this ONCE from the Apps Script editor after pasting the code. */
function setup() {
  ensureSheets_();
  seedDirectory_();
  return 'RCube backend ready. Tabs created and demo creators seeded.';
}

/**
 * Wipe all RCube tabs and recreate + reseed them from scratch.
 * Use this to clear bad data (e.g. #ERROR! phone cells from an older version).
 */
function reset() {
  var ss = ss_();
  var tmp = ss.insertSheet('__tmp__');           // keep >=1 sheet so deletes are allowed
  Object.keys(SHEETS).forEach(function (name) {
    var sh = ss.getSheetByName(name);
    if (sh) ss.deleteSheet(sh);
  });
  ensureSheets_();
  seedDirectory_();
  var t = ss.getSheetByName('__tmp__');
  if (t) ss.deleteSheet(t);
  return 'RCube reset: tabs recreated and demo creators reseeded (phones stored as text).';
}

function ensureSheets_() {
  Object.keys(SHEETS).forEach(function (name) { sheet_(name); });
}

function seedDirectory_() {
  if (rows_('creator_profiles').length > 0) return; // idempotent

  // lat/lng are real Bengaluru neighbourhoods so distance is computed per organizer.
  var seed = [
    { name: 'Meera', cat: 'GUITARIST', lat: 12.9352, lng: 77.6245, bio: 'Acoustic guitarist & vocalist. Soulful covers for warm evenings.', ig: '@meera.strings', langs: 'English,Kannada', done: 27, svc: [['30 Minute Live Performance', 250000, 30, ''], ['60 Minute Live Performance', 400000, 60, 'Covers + requests.']] },
    { name: 'Sam', cat: 'GUITARIST', lat: 12.9116, lng: 77.6389, bio: 'Rock & blues guitarist. High energy sets for parties and fests.', ig: '@sam.plays', langs: 'English,Hindi', done: 15, svc: [['45 Minute Set', 300000, 45, ''], ['90 Minute Set', 550000, 90, '']] },
    { name: 'Priya', cat: 'PHOTOGRAPHER', lat: 12.9719, lng: 77.6412, bio: 'Candid & portrait photographer. I chase real moments, not poses.', ig: '@priya.frames', langs: 'English', done: 41, svc: [['Birthday Photoshoot', 500000, 120, '2 hours, edited gallery.'], ['Wedding Photography', 1800000, '', 'Full day coverage.']] },
    { name: 'Sneha', cat: 'MEHENDI_ARTIST', lat: 12.9250, lng: 77.5938, bio: 'Bridal & party mehendi. Intricate, long-lasting designs.', ig: '@sneha.mehendi', langs: 'Hindi,English', done: 33, svc: [['Party Mehendi', 250000, '', 'Up to 5 guests.'], ['Bridal Mehendi', 700000, '', 'Full bridal, both hands & feet.']] },
    { name: 'Kabir', cat: 'SINGER', lat: 12.9698, lng: 77.7500, bio: 'Playback-style vocalist. Bollywood, ghazals and unplugged.', ig: '', langs: 'Hindi,Urdu,English', done: 19, svc: [['Unplugged Evening', 450000, 60, '']] },
    { name: 'Rhea', cat: 'DANCER', lat: 13.0035, lng: 77.5647, bio: 'Contemporary & semi-classical performances for stage events.', ig: '@rhea.moves', langs: 'English,Tamil', done: 22, svc: [['Solo Performance', 400000, 15, 'One choreographed piece.'], ['Event Set', 700000, 40, 'Three pieces.']] }
  ];

  seed.forEach(function (c, idx) {
    var uid = 'seed_u_' + idx;
    appendObj_('users', {
      id: uid, phone: '+91 900000000' + idx,
      firstName: c.name, lastName: '', displayName: c.name,
      defaultMode: 'creator', aadhaarStatus: 'VERIFIED',
      aadhaarFront: 'uploaded', aadhaarBack: 'uploaded', createdAt: Date.now()
    });
    var pid = 'seed_p_' + idx;
    appendObj_('creator_profiles', {
      id: pid, userId: uid, displayName: c.name, category: c.cat, bio: c.bio,
      city: 'Bengaluru', languages: c.langs, status: 'APPROVED', active: true,
      instagram: c.ig, youtube: '', completedBookings: c.done,
      rejectionReason: '', lat: c.lat, lng: c.lng, distanceKm: '', createdAt: Date.now()
    });
    c.svc.forEach(function (s, si) {
      appendObj_('services', {
        id: 'seed_s_' + idx + '_' + si, profileId: pid, title: s[0],
        pricePaise: s[1], durationMinutes: s[2], description: s[3],
        isActive: true, createdAt: Date.now()
      });
    });
  });

  // A few seed reviews so the ratings/comments feature is visible on day one.
  var seedReviews = [
    ['seed_p_0', 'Anita', 5, 'Meera set the perfect mood for our terrace dinner. Highly recommend!'],
    ['seed_p_0', 'Rohit', 4, 'Lovely voice and song choices. Started a little late but worth it.'],
    ['seed_p_2', 'Divya', 5, 'Priya captured candids we did not even know were happening. Stunning gallery.'],
    ['seed_p_3', 'Farah', 5, 'Sneha\u2019s bridal mehendi was intricate and lasted beautifully.'],
    ['seed_p_5', 'Nikhil', 4, 'Rhea\u2019s set was a hit at the college fest.']
  ];
  seedReviews.forEach(function (r, i) {
    appendObj_('reviews', {
      id: 'seed_rev_' + i, bookingId: 'seed_b_' + i, profileId: r[0],
      organizerName: r[1], rating: r[2], comment: r[3], createdAt: Date.now() - i * 86400000
    });
  });
}
