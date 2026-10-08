/* ══════════════════════════════════════
   CARENET — main.js
   Fully connected to Spring Boot backend
   Base URL: http://localhost:8080
══════════════════════════════════════ */
 
const API = 'http://localhost:8080/api';
 
/* ──────────────────────────────────────
   STATE
────────────────────────────────────── */
let currentPortal = 'patient';
let authToken     = null;
let currentUser   = null;
 
/* ──────────────────────────────────────
   HELPERS
────────────────────────────────────── */
function authHeaders() {
  return { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + authToken };
}
 
function saveSession(data) {
  authToken   = data.token;
  currentUser = { id: data.id, name: data.name, role: data.role };
  sessionStorage.setItem('cn_token', data.token);
  sessionStorage.setItem('cn_user',  JSON.stringify(currentUser));
}
 
function loadSession() {
  authToken   = sessionStorage.getItem('cn_token');
  const u     = sessionStorage.getItem('cn_user');
  currentUser = u ? JSON.parse(u) : null;
}
 
function clearSession() {
  authToken = null; currentUser = null;
  sessionStorage.removeItem('cn_token');
  sessionStorage.removeItem('cn_user');
}
 
function badgeClass(status) {
  return { CONFIRMED:'badge-green', COMPLETED:'badge-green',
           PENDING:'badge-orange',  CANCELLED:'badge-orange',
           ONLINE:'badge-blue' }[status] || 'badge-blue';
}
 
function formatDate(str) {
  if (!str) return '—';
  try { return new Date(str).toLocaleDateString('en-IN', { day:'numeric', month:'short', year:'numeric' }); }
  catch { return str; }
}
 
/* ──────────────────────────────────────
   PAGE NAVIGATION
────────────────────────────────────── */
function showPage(page) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.getElementById('page-' + page).classList.add('active');
  window.scrollTo(0, 0);
}
 
/* ──────────────────────────────────────
   AUTH MODAL
────────────────────────────────────── */
function openAuthModal(type) {
  currentPortal = type;
  document.getElementById('modal-emoji').textContent = type === 'patient' ? '🧑‍⚕️' : '👨‍⚕️';
  document.getElementById('modal-title').textContent = type === 'patient' ? 'Patient Login' : 'Doctor Login';
  document.getElementById('modal-desc').textContent  = type === 'patient'
    ? 'Access your health records and appointments'
    : 'Access your professional dashboard';
  document.getElementById('auth-modal').classList.add('open');
  switchAuthTab('login');
  document.getElementById('login-btn').onclick           = handlePatientLogin;
  document.getElementById('register-btn').onclick        = handlePatientRegister;
  document.getElementById('doctor-login-btn').onclick    = handleDoctorLogin;
  document.getElementById('doctor-register-btn').onclick = handleDoctorRegister;
}
 
function closeAuthModal() {
  document.getElementById('auth-modal').classList.remove('open');
}
 
document.getElementById('auth-modal').addEventListener('click', function(e) {
  if (e.target === this) closeAuthModal();
});
 
function switchAuthTab(tab) {
  const isDoc = currentPortal === 'doctor';
  ['patient-login-form','patient-register-form','doctor-login-form','doctor-register-form']
    .forEach(id => document.getElementById(id).style.display = 'none');
  document.getElementById(isDoc
    ? (tab === 'login' ? 'doctor-login-form' : 'doctor-register-form')
    : (tab === 'login' ? 'patient-login-form' : 'patient-register-form')
  ).style.display = 'block';
  document.getElementById('tab-login').classList.toggle('active', tab === 'login');
  document.getElementById('tab-register').classList.toggle('active', tab === 'register');
}
 
/* ──────────────────────────────────────
   AUTH HANDLERS
────────────────────────────────────── */
async function handlePatientLogin() {
  const form       = document.getElementById('patient-login-form');
  const inputs     = form.querySelectorAll('input');
  const identifier = inputs[0].value.trim();
  const password   = inputs[1].value;
  if (!identifier || !password) { showToast('❌ Fill in all fields.'); return; }
  try {
    showToast('⏳ Logging in…');
    const res  = await fetch(`${API}/auth/patient/login`, {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ identifier, password })
    });
    const data = await res.json();
    if (!data.success) { showToast('❌ ' + data.message); return; }
    saveSession(data.data);
    closeAuthModal();
    showToast('✅ Welcome, ' + currentUser.name + '!');
    document.querySelector('#page-patient .sp-name').textContent = currentUser.name;
    showPage('patient');
    loadPatientDashboard();
    loadPatientProfile();
  } catch { showToast('❌ Cannot reach server.'); }
}
 
async function handlePatientRegister() {
  const form   = document.getElementById('patient-register-form');
  const inputs = form.querySelectorAll('input');
  const body   = { name: inputs[0].value.trim(), abhaId: inputs[1].value.trim(),
                   phone: inputs[2].value.trim(), password: inputs[3].value };
  if (!body.name || !body.abhaId || !body.phone || !body.password)
    { showToast('❌ Fill in all fields.'); return; }
  try {
    showToast('⏳ Creating account…');
    const res  = await fetch(`${API}/auth/patient/register`, {
      method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)
    });
    const data = await res.json();
    if (!data.success) { showToast('❌ ' + data.message); return; }
    saveSession(data.data);
    closeAuthModal();
    showToast('✅ Account created! Welcome, ' + currentUser.name + '!');
    document.querySelector('#page-patient .sp-name').textContent = currentUser.name;
    showPage('patient');
    loadPatientDashboard();
    loadPatientProfile();
  } catch { showToast('❌ Cannot reach server.'); }
}
 
async function handleDoctorLogin() {
  const doctorCode = document.getElementById('dlogin-code')?.value.trim()    || '';
  const email      = document.getElementById('dlogin-email')?.value.trim()   || '';
  const password   = document.getElementById('dlogin-password')?.value       || '';
 
  if (!doctorCode) { showToast('❌ Doctor ID is required.');  return; }
  if (!email)      { showToast('❌ Email is required.');       return; }
  if (!password)   { showToast('❌ Password is required.');    return; }
 
  try {
    showToast('⏳ Logging in…');
    const res  = await fetch(`${API}/auth/doctor/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ doctorCode, email, password })
    });
    const data = await res.json();
    if (!data.success) {
      showToast('❌ ' + data.message);
      return;
    }
    saveSession(data.data);
    closeAuthModal();
    showToast('✅ Welcome, ' + currentUser.name + '!');
    showPage('doctor');
    loadDoctorDashboard();
    loadDoctorProfile();
  } catch { showToast('❌ Cannot connect to server. Is the backend running?'); }
}
 
async function handleDoctorRegister() {
  // Read all fields by ID
  const name        = document.getElementById('dreg-name')?.value.trim()       || '';
  const spec        = document.getElementById('dreg-spec')?.value               || '';
  const hospital    = document.getElementById('dreg-hospital')?.value.trim()    || '';
  const hospitalId  = document.getElementById('dreg-hospitalid')?.value.trim()  || '';
  const email       = document.getElementById('dreg-email')?.value.trim()       || '';
  const license     = document.getElementById('dreg-license')?.value.trim()     || '';
  const password    = document.getElementById('dreg-password')?.value           || '';
  const confirm     = document.getElementById('dreg-confirm')?.value            || '';
 
  // Validate
  if (!name)       { showToast('❌ Full name is required.');         return; }
  if (!spec)       { showToast('❌ Please select specialization.');  return; }
  if (!hospital)   { showToast('❌ Hospital name is required.');     return; }
  if (!hospitalId) { showToast('❌ Hospital ID is required.');       return; }
  if (!email)      { showToast('❌ Work email is required.');        return; }
  if (!password)   { showToast('❌ Password is required.');          return; }
  if (password.length < 6) { showToast('❌ Password must be at least 6 characters.'); return; }
  if (password !== confirm) { showToast('❌ Passwords do not match.'); return; }
 
  // Auto-generate unique Doctor ID from name + timestamp
  const namePart  = name.replace(/Dr\.?\s*/i,'').split(' ')[0].toUpperCase().substring(0,3);
  const doctorCode = 'DOC-' + namePart + '-' + Date.now().toString().slice(-5);
 
  const body = { name, specialization: spec, hospital, hospitalId,
                 email, medicalLicenseNo: license, password, doctorCode };
 
  try {
    showToast('⏳ Registering…');
    const res  = await fetch(`${API}/auth/doctor/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    const data = await res.json();
    if (!data.success) { showToast('❌ ' + data.message); return; }
 
    saveSession(data.data);
    closeAuthModal();
 
    // Show the Doctor ID prominently so they can use it to login later
    showDoctorIdAlert(doctorCode, name);
 
    showPage('doctor');
    loadDoctorDashboard();
    loadDoctorProfile();
  } catch { showToast('❌ Cannot connect to server.'); }
}
 
function showDoctorIdAlert(doctorCode, name) {
  // Remove any existing alert
  document.getElementById('docid-alert')?.remove();
 
  const alert = document.createElement('div');
  alert.id    = 'docid-alert';
  alert.style.cssText = `
    position:fixed; top:80px; left:50%; transform:translateX(-50%);
    background:white; border:2px solid var(--teal); border-radius:18px;
    padding:28px 36px; z-index:9999; box-shadow:0 12px 40px rgba(0,0,0,0.18);
    max-width:420px; width:90%; text-align:center;
    font-family:'Nunito',sans-serif;
  `;
  alert.innerHTML = `
    <div style="font-size:2rem;margin-bottom:12px">🎉</div>
    <h3 style="font-size:1.1rem;font-weight:900;color:var(--text-dark);margin-bottom:8px">
      Welcome to CareNet, ${name.split(' ')[0]}!
    </h3>
    <p style="font-size:0.88rem;color:var(--text-soft);margin-bottom:16px">
      Your registration is complete. Save your Doctor ID — you will need it every time you log in.
    </p>
    <div style="background:var(--mint);border-radius:12px;padding:14px 20px;margin-bottom:20px">
      <div style="font-size:0.75rem;font-weight:800;color:var(--text-soft);margin-bottom:4px;text-transform:uppercase;letter-spacing:1px">Your Doctor ID</div>
      <div style="font-size:1.4rem;font-weight:900;color:var(--teal-dark);letter-spacing:1px">${doctorCode}</div>
      <div style="font-size:0.75rem;color:var(--text-soft);margin-top:4px">Use this + your email + password to login</div>
    </div>
    <button onclick="navigator.clipboard.writeText('${doctorCode}').then(()=>showToast('📋 Doctor ID copied!')); "
      style="background:var(--teal);color:white;border:none;border-radius:30px;padding:10px 24px;
             font-weight:800;font-size:0.9rem;cursor:pointer;font-family:inherit;margin-right:8px">
      📋 Copy ID
    </button>
    <button onclick="document.getElementById('docid-alert').remove()"
      style="background:var(--bg);color:var(--text-mid);border:1.5px solid var(--border);border-radius:30px;
             padding:10px 24px;font-weight:800;font-size:0.9rem;cursor:pointer;font-family:inherit">
      Got it ✓
    </button>
  `;
  document.body.appendChild(alert);
}
 
function logout() { clearSession(); showPage('home'); showToast('👋 Logged out.'); }
 
/* ──────────────────────────────────────
   PORTAL TAB SWITCHING
────────────────────────────────────── */
function switchTab(portal, tab, btn) {
  document.querySelectorAll('#page-' + portal + ' .portal-tab')
    .forEach(t => t.style.display = 'none');
  document.getElementById(portal + '-tab-' + tab).style.display = 'block';
  document.querySelectorAll('#page-' + portal + ' .sidebar-link')
    .forEach(l => l.classList.remove('active'));
  if (btn) btn.classList.add('active');
 
  if (portal === 'p') {
    if (tab === 'dashboard')    loadPatientDashboard();
    if (tab === 'records')      loadPatientRecords();
    if (tab === 'find-doctors') loadDoctors();
    if (tab === 'appointments') loadPatientAppointments();
    if (tab === 'profile')      loadPatientProfile();
  }
  if (portal === 'd') {
    if (tab === 'dashboard')    loadDoctorDashboard();
    if (tab === 'appointments') loadDoctorAppointments();
    if (tab === 'patients')     loadDoctorPatients();
    if (tab === 'profile')      loadDoctorProfile();
  }
}
 
/* ──────────────────────────────────────
   TOAST
────────────────────────────────────── */
function showToast(msg) {
  const t = document.createElement('div');
  t.className = 'toast'; t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 3500);
}
 
document.addEventListener('click', function(e) {
  if (e.target.classList.contains('chip')) {
    e.target.closest('.filter-chips').querySelectorAll('.chip')
      .forEach(c => c.classList.remove('active'));
    e.target.classList.add('active');
  }
});
 
 
/* ──────────────────────────────────────
   LOAD PROFILE FROM DATABASE
────────────────────────────────────── */
 
async function loadPatientProfile() {
  if (!authToken) return;
  try {
    const res  = await fetch(`${API}/patient/profile`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    const p = data.data;
 
    // Sidebar
    const initial = (p.name || 'P').charAt(0).toUpperCase();
    const avatar  = document.getElementById('p-avatar');
    const sname   = document.getElementById('p-sidebar-name');
    const sabha   = document.getElementById('p-sidebar-abha');
    if (avatar) avatar.textContent = initial;
    if (sname)  sname.textContent  = p.name || '—';
    if (sabha)  sabha.textContent  = 'ABHA: ' + (p.abhaId || '—');
 
    // Dashboard heading
    const heading = document.getElementById('p-welcome-heading');
    const date    = document.getElementById('p-dashboard-date');
    if (heading) heading.textContent = 'Welcome back, ' + (p.name?.split(' ')[0] || '') + ' 👋';
    if (date)    date.textContent    = "Here's your health summary for today, " +
      new Date().toLocaleDateString('en-IN', { day:'numeric', month:'long', year:'numeric' });
 
    // Profile form
    const set = (id, val) => { const el = document.getElementById(id); if (el) el.value = val || ''; };
    set('profile-name',  p.name);
    set('profile-abha',  p.abhaId);
    set('profile-phone', p.phone);
    set('profile-age',   p.age);
    set('profile-notes', p.allergiesNotes);
 
    // Blood group & gender dropdowns
    const bg = document.querySelector('#p-tab-profile select');
    if (bg && p.bloodGroup) {
      [...bg.options].forEach(o => { if (o.value === p.bloodGroup || o.text === p.bloodGroup) bg.value = o.value; });
    }
  } catch (e) { console.error('loadPatientProfile:', e); }
}
 
async function savePatientProfile() {
  if (!authToken) return;
  const body = {
    name:           document.getElementById('profile-name')?.value  || '',
    age:            parseInt(document.getElementById('profile-age')?.value) || null,
    bloodGroup:     document.querySelector('#p-tab-profile select')?.value  || '',
    allergiesNotes: document.getElementById('profile-notes')?.value || ''
  };
  try {
    const res  = await fetch(`${API}/patient/profile`, {
      method: 'PUT', headers: authHeaders(), body: JSON.stringify(body)
    });
    const data = await res.json();
    showToast(data.success ? '✅ Profile updated successfully!' : '❌ ' + data.message);
    if (data.success) loadPatientProfile();
  } catch { showToast('❌ Error saving profile.'); }
}
 
async function loadDoctorProfile() {
  if (!authToken) return;
  try {
    const res  = await fetch(`${API}/doctor/profile`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    const d = data.data;
 
    // Sidebar
    const initial = (d.name || 'D').charAt(0).toUpperCase();
    const avatar  = document.getElementById('d-avatar');
    const sname   = document.getElementById('d-sidebar-name');
    const sspec   = document.getElementById('d-sidebar-spec');
    if (avatar) avatar.textContent = initial;
    if (sname)  sname.textContent  = d.name || '—';
    if (sspec)  sspec.textContent  = (d.specialization || '') + (d.hospital ? ' · ' + d.hospital : '');
 
    // Dashboard heading
    const heading  = document.getElementById('d-welcome-heading');
    const subtitle = document.getElementById('d-dashboard-subtitle');
    if (heading)  heading.textContent  = 'Doctor Dashboard 👨‍⚕️';
    if (subtitle) subtitle.textContent = 'Good day, ' + (d.name || 'Doctor') + '! Here is your overview.';
 
    // Profile form
    const set = (id, val) => { const el = document.getElementById(id); if (el) el.value = val || ''; };
    set('d-profile-name',     d.name);
    set('d-profile-spec',     d.specialization);
    set('d-profile-hospital', d.hospital);
    set('d-profile-exp',      d.experienceYears);
    set('d-profile-phone',    d.phone);
    set('d-profile-fee',      d.consultationFee);
    set('d-profile-bio',      d.bio);
  } catch (e) { console.error('loadDoctorProfile:', e); }
}
 
async function saveDoctorProfile() {
  if (!authToken) return;
  const body = {
    name:           document.getElementById('d-profile-name')?.value     || '',
    specialization: document.getElementById('d-profile-spec')?.value     || '',
    hospital:       document.getElementById('d-profile-hospital')?.value  || '',
    experienceYears:parseInt(document.getElementById('d-profile-exp')?.value) || null,
    consultationFee:parseInt(document.getElementById('d-profile-fee')?.value) || null,
    bio:            document.getElementById('d-profile-bio')?.value       || ''
  };
  try {
    const res  = await fetch(`${API}/doctor/profile`, {
      method: 'PUT', headers: authHeaders(), body: JSON.stringify(body)
    });
    const data = await res.json();
    showToast(data.success ? '✅ Profile updated successfully!' : '❌ ' + data.message);
    if (data.success) loadDoctorProfile();
  } catch { showToast('❌ Error saving profile.'); }
}
 
/* ══════════════════════════════════════
   PATIENT — API
══════════════════════════════════════ */
async function loadPatientDashboard() {
  if (!authToken) return;
 
  // Reset all stats to 0 while loading
  const setEl = (id, val) => { const e = document.getElementById(id); if (e) e.textContent = val; };
 
  try {
    const res  = await fetch(`${API}/patient/dashboard`, { headers: authHeaders() });
    const data = await res.json();
    if (data.success) {
      const d = data.data;
      const records = d.totalRecords         ?? 0;
      const appts   = d.upcomingAppointments ?? 0;
      const doctors = d.totalDoctorsConsulted?? 0;
 
      setEl('p-stat-records',      records);
      setEl('p-stat-appts',        appts);
      setEl('p-stat-doctors',      doctors);
      setEl('p-stat-hospitals',    doctors); // hospitals = unique doctors' hospitals
 
      // Sub-labels
      setEl('p-stat-records-sub',   records === 0 ? 'No records yet'       : records + ' total records');
      setEl('p-stat-appts-sub',     appts   === 0 ? 'No upcoming'          : appts + ' upcoming');
      setEl('p-stat-doctors-sub',   doctors === 0 ? 'None consulted yet'   : doctors + ' doctor(s)');
      setEl('p-stat-hospitals-sub', doctors === 0 ? 'Visit a doctor first' : 'Based on consultations');
    }
  } catch {}
 
  loadDashboardAppointments();
  loadDashboardRecords();
}
 
async function loadDashboardAppointments() {
  if (!authToken) return;
  try {
    const res  = await fetch(`${API}/patient/appointments`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    const box  = document.getElementById('p-dash-appts');
    if (!box) return;
    const list = data.data.filter(a => a.status !== 'CANCELLED' && a.status !== 'COMPLETED').slice(0,3);
    box.innerHTML = list.length ? list.map(a => `
      <div class="appt-item">
        <div class="appt-avatar" style="background:#2db89e">👨‍⚕️</div>
        <div class="appt-info"><h4>${a.doctorName}</h4><p>${a.doctorSpecialization} · ${a.hospital}</p>
          <span class="badge ${badgeClass(a.status)}">${a.status}</span></div>
        <div class="appt-time"><div class="time">${(a.time||'').substring(0,5)}</div><div class="date">${formatDate(a.date)}</div></div>
      </div>`).join('')
    : '<p style="color:var(--text-soft);font-size:0.88rem;padding:10px 0">No upcoming appointments.</p>';
  } catch {}
}
 
async function loadDashboardRecords() {
  if (!authToken) return;
  try {
    const res  = await fetch(`${API}/patient/records`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    const box  = document.getElementById('p-dash-records');
    if (!box) return;
    const icons= { BLOOD_TEST:'🩸',PRESCRIPTION:'💊',ECG:'🫀',XRAY_RADIOLOGY:'🦴',
                   DIAGNOSIS_REPORT:'📋',DISCHARGE_SUMMARY:'🏥',MRI:'🧠',THYROID_PANEL:'🔬',OTHER:'📄' };
    const clrs = ['fi-teal','fi-coral','fi-lavender','fi-amber'];
    const list = data.data.slice(0,4);
    box.innerHTML = list.length ? list.map((r,i) => `
      <div class="rec-item">
        <div class="rec-icon-box ${clrs[i%clrs.length]}">${icons[r.recordType]||'📄'}</div>
        <div class="rec-info"><div class="rn">${r.recordType.replace(/_/g,' ')}</div>
          <div class="rs">${r.hospital} · ${formatDate(r.uploadDate)}</div>
          ${r.diagnosis?`<div class="rs" style="margin-top:2px">Dx: ${r.diagnosis}</div>`:''}
          ${r.prescription?`<div class="rs" style="margin-top:2px">Rx: ${r.prescription}</div>`:''}
        </div>
        ${r.downloadUrl
          ?`<button class="rec-download" onclick="downloadRecord('${r.downloadUrl}','${r.reportFileName}')">↓ PDF</button>`
          :'<span style="font-size:0.74rem;color:var(--text-soft);margin-left:auto">No file</span>'}
      </div>`).join('')
    : '<p style="color:var(--text-soft);font-size:0.88rem;padding:10px 0">No records yet.</p>';
  } catch {}
}
 
async function loadPatientRecords() {
  if (!authToken) return;
  const box = document.getElementById('records-list');
  if (!box) return;
  box.innerHTML = '<p style="padding:14px;color:var(--text-soft)">⏳ Loading records…</p>';
  try {
    const res  = await fetch(`${API}/patient/records`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) { box.innerHTML='<p style="padding:14px;color:var(--coral)">Failed to load.</p>'; return; }
    const icons= { BLOOD_TEST:'🩸',PRESCRIPTION:'💊',ECG:'🫀',XRAY_RADIOLOGY:'🦴',
                   DIAGNOSIS_REPORT:'📋',DISCHARGE_SUMMARY:'🏥',MRI:'🧠',THYROID_PANEL:'🔬',OTHER:'📄' };
    const clrs = ['fi-teal','fi-coral','fi-lavender','fi-amber','fi-sky','fi-pink'];
    box.innerHTML = data.data.length ? data.data.map((r,i) => `
      <div class="rec-item">
        <div class="rec-icon-box ${clrs[i%clrs.length]}">${icons[r.recordType]||'📄'}</div>
        <div class="rec-info">
          <div class="rn">${r.recordType.replace(/_/g,' ')}</div>
          <div class="rs">${r.hospital} · ${r.doctorName} · ${formatDate(r.uploadDate)}</div>
          ${r.diagnosis?`<div class="rs" style="margin-top:3px;color:var(--text-dark)">🩺 Dx: ${r.diagnosis}</div>`:''}
          ${r.prescription?`<div class="rs" style="margin-top:2px;color:var(--text-dark)">💊 Rx: ${r.prescription}</div>`:''}
          ${r.notes?`<div class="rs" style="margin-top:2px">📝 ${r.notes}</div>`:''}
        </div>
        <span class="badge badge-green" style="margin-right:8px;white-space:nowrap">${r.recordType.replace(/_/g,' ')}</span>
        ${r.downloadUrl
          ?`<button class="rec-download" onclick="downloadRecord('${r.downloadUrl}','${r.reportFileName}')">↓ PDF</button>`
          :'<span style="font-size:0.74rem;color:var(--text-soft)">No file</span>'}
      </div>`).join('')
    : '<p style="padding:14px;color:var(--text-soft)">No medical records found.</p>';
  } catch { box.innerHTML='<p style="padding:14px;color:var(--coral)">Error loading records.</p>'; }
}
 
async function loadPatientAppointments() {
  if (!authToken) return;
  const tbody = document.getElementById('p-appt-tbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="7" style="padding:18px;color:var(--text-soft)">⏳ Loading…</td></tr>';
  try {
    const res  = await fetch(`${API}/patient/appointments`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    tbody.innerHTML = data.data.length ? data.data.map(a => `
      <tr>
        <td><div class="patient-cell"><div class="pat-av" style="background:#e8f8f3">👨‍⚕️</div>${a.doctorName}</div></td>
        <td>${a.doctorSpecialization}</td><td>${a.hospital}</td>
        <td>${formatDate(a.date)}</td><td>${(a.time||'').substring(0,5)}</td>
        <td><span class="badge ${badgeClass(a.status)}">${a.status}</span></td>
        <td>${(a.status==='PENDING'||a.status==='CONFIRMED')
          ?`<button class="btn btn-sm btn-outline" onclick="cancelAppointment(${a.appointmentId})">Cancel</button>`
          :`<span style="font-size:0.78rem;color:var(--text-soft)">${a.status}</span>`}</td>
      </tr>`).join('')
    : '<tr><td colspan="7" style="padding:18px;color:var(--text-soft)">No appointments found.</td></tr>';
  } catch { tbody.innerHTML='<tr><td colspan="7" style="padding:18px;color:var(--coral)">Error.</td></tr>'; }
}
 
async function cancelAppointment(id) {
  if (!confirm('Cancel this appointment?')) return;
  try {
    const res  = await fetch(`${API}/patient/appointments/${id}/cancel`,
      { method:'PATCH', headers: authHeaders() });
    const data = await res.json();
    showToast(data.success ? '✅ Cancelled.' : '❌ ' + data.message);
    if (data.success) loadPatientAppointments();
  } catch { showToast('❌ Error.'); }
}
 
async function loadDoctors() {
  const box = document.getElementById('doctors-grid');
  if (!box) return;
  box.innerHTML = '<p style="padding:14px;color:var(--text-soft)">⏳ Loading doctors…</p>';
  try {
    const res  = await fetch(`${API}/doctors/all`);
    const data = await res.json();
    if (!data.success || !data.data.length) {
      box.innerHTML='<p style="padding:14px;color:var(--text-soft)">No doctors registered yet.</p>'; return;
    }
    const clrs = ['#e8f8f3','#ede9ff','#e3f3ff','#fff3e0','#fce4ec','#e8f8f3'];
    box.innerHTML = data.data.map((d,i) => `
      <div class="doctor-card">
        <div class="dr-top">
          <div class="dr-avatar" style="background:${clrs[i%clrs.length]}">👨‍⚕️</div>
          <div class="dr-info"><h3>${d.name}</h3>
            <div class="dr-spec">${d.specialization}</div>
            <div class="dr-hosp">📍 ${d.hospital||'CareNet Hospital'}</div></div>
        </div>
        <div class="dr-stats">
          <div class="dr-stat"><div class="ds-n">${d.rating?d.rating.toFixed(1)+'⭐':'New'}</div><div class="ds-l">Rating</div></div>
          <div class="dr-stat"><div class="ds-n">${d.experienceYears?d.experienceYears+'yr':'—'}</div><div class="ds-l">Exp</div></div>
          <div class="dr-stat"><div class="ds-n">${d.consultationFee?'₹'+d.consultationFee:'—'}</div><div class="ds-l">Fee</div></div>
        </div>
        <div class="dr-avail"><div class="avail-dot"></div>${d.available?'Available Today':'Unavailable'}</div>
        <button class="btn btn-primary btn-sm" style="width:100%;justify-content:center"
          onclick="openBookingModal(${d.doctorId},'${d.name}','${d.specialization}')">Book Appointment</button>
      </div>`).join('');
  } catch { box.innerHTML='<p style="padding:14px;color:var(--coral)">Error loading.</p>'; }
}
 
function openBookingModal(doctorId, doctorName, spec) {
  document.getElementById('booking-overlay')?.remove();
  const today = new Date().toISOString().split('T')[0];
  document.body.insertAdjacentHTML('beforeend', `
    <div id="booking-overlay" style="position:fixed;inset:0;background:rgba(0,0,0,0.4);z-index:600;
      display:flex;align-items:center;justify-content:center;backdrop-filter:blur(4px)">
      <div style="background:white;border-radius:20px;padding:36px;width:420px;max-width:95vw;
        box-shadow:0 20px 60px rgba(0,0,0,0.18)">
        <h3 style="font-size:1.2rem;font-weight:900;margin-bottom:4px">Book Appointment</h3>
        <p style="color:var(--text-soft);font-size:0.88rem;margin-bottom:22px">${doctorName} · ${spec}</p>
        <div class="form-group" style="margin-bottom:14px">
          <div class="form-label">Date</div>
          <input type="date" id="book-date" class="form-input" style="width:100%" min="${today}" value="${today}"/>
        </div>
        <div class="form-group" style="margin-bottom:14px">
          <div class="form-label">Time</div>
          <input type="time" id="book-time" class="form-input" style="width:100%" value="10:00"/>
        </div>
        <div class="form-group" style="margin-bottom:14px">
          <div class="form-label">Type</div>
          <select id="book-type" class="form-select" style="width:100%">
            <option value="CONSULTATION">Consultation</option>
            <option value="FOLLOW_UP">Follow Up</option>
            <option value="NEW_PATIENT">New Patient</option>
            <option value="EMERGENCY">Emergency</option>
          </select>
        </div>
        <div class="form-group" style="margin-bottom:22px">
          <div class="form-label">Notes (optional)</div>
          <textarea id="book-notes" class="form-textarea" style="width:100%" placeholder="Describe your symptoms…"></textarea>
        </div>
        <div style="display:flex;gap:12px">
          <button class="btn btn-primary" style="flex:1;justify-content:center"
            onclick="confirmBooking(${doctorId})">Confirm Booking</button>
          <button class="btn btn-outline" onclick="document.getElementById('booking-overlay').remove()">Cancel</button>
        </div>
      </div>
    </div>`);
}
 
async function confirmBooking(doctorId) {
  const date  = document.getElementById('book-date').value;
  const time  = document.getElementById('book-time').value + ':00';
  const type  = document.getElementById('book-type').value;
  const notes = document.getElementById('book-notes').value;
  if (!date || !time) { showToast('❌ Select date and time.'); return; }
  try {
    showToast('⏳ Booking…');
    const res  = await fetch(`${API}/patient/appointments`, {
      method:'POST', headers: authHeaders(),
      body: JSON.stringify({ doctorId, date, time, type, notes })
    });
    const data = await res.json();
    document.getElementById('booking-overlay').remove();
    showToast(data.success ? '✅ Appointment booked!' : '❌ ' + data.message);
    if (data.success) loadPatientDashboard();
  } catch { showToast('❌ Error booking.'); }
}
 
/* ══════════════════════════════════════
   DOCTOR — API
══════════════════════════════════════ */
async function loadDoctorDashboard() {
  if (!authToken) return;
  const setEl = (id, val) => { const e = document.getElementById(id); if (e) e.textContent = val; };
 
  try {
    const res  = await fetch(`${API}/doctor/dashboard`, { headers: authHeaders() });
    const data = await res.json();
    if (data.success) {
      const d       = data.data;
      const today   = d.todayAppointments    ?? 0;
      const patients= d.totalPatients        ?? 0;
      const reports = d.totalReportsUploaded ?? 0;
      const rating  = d.rating ? d.rating.toFixed(1) : '—';
 
      setEl('d-stat-today',    today);
      setEl('d-stat-patients', patients);
      setEl('d-stat-reports',  reports);
      setEl('d-stat-rating',   rating);
 
      setEl('d-stat-today-sub',    today    === 0 ? 'No appointments today'    : today + ' scheduled today');
      setEl('d-stat-patients-sub', patients === 0 ? 'No patients yet'          : patients + ' unique patient(s)');
      setEl('d-stat-reports-sub',  reports  === 0 ? 'No reports uploaded yet'  : reports + ' report(s) uploaded');
      setEl('d-stat-rating-sub',   rating   === '—' ? 'No ratings yet'         : 'Average patient rating');
    }
  } catch {}
 
  loadDoctorTodayAppts();
}
 
async function loadDoctorTodayAppts() {
  if (!authToken) return;
  try {
    const res  = await fetch(`${API}/doctor/appointments/today`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    const box  = document.getElementById('d-dash-today');
    if (!box) return;
    box.innerHTML = data.data.length ? data.data.map(a => `
      <div class="appt-item">
        <div class="appt-avatar" style="background:#7b6cf6">${a.patientName.charAt(0)}</div>
        <div class="appt-info"><h4>${a.patientName}</h4>
          <p>ABHA: ${a.patientAbhaId} · ${(a.type||'').replace(/_/g,' ')}</p>
          <span class="badge ${badgeClass(a.status)}">${a.status}</span></div>
        <div class="appt-time"><div class="time">${(a.time||'').substring(0,5)}</div><div class="date">Today</div></div>
      </div>`).join('')
    : '<p style="color:var(--text-soft);font-size:0.88rem;padding:10px 0">No appointments today.</p>';
  } catch {}
}
 
async function loadDoctorAppointments() {
  if (!authToken) return;
  const tbody = document.getElementById('d-appt-tbody');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="7" style="padding:18px;color:var(--text-soft)">⏳ Loading…</td></tr>';
  try {
    const res  = await fetch(`${API}/doctor/appointments`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    tbody.innerHTML = data.data.length ? data.data.map(a => `
      <tr>
        <td><div class="patient-cell"><div class="pat-av" style="background:#ede9ff">${a.patientName.charAt(0)}</div>${a.patientName}</div></td>
        <td style="font-size:0.78rem;color:var(--text-soft);font-weight:700">${a.patientAbhaId}</td>
        <td>${formatDate(a.date)}</td><td>${(a.time||'').substring(0,5)}</td>
        <td>${(a.type||'').replace(/_/g,' ')}</td>
        <td><span class="badge ${badgeClass(a.status)}">${a.status}</span></td>
        <td style="display:flex;gap:6px;flex-wrap:wrap">
          ${a.status==='PENDING'?`<button class="btn btn-sm btn-primary" onclick="updateStatus(${a.appointmentId},'CONFIRMED')">Confirm</button>`:''}
          ${a.status==='CONFIRMED'?`<button class="btn btn-sm btn-outline" onclick="updateStatus(${a.appointmentId},'COMPLETED')">Complete</button>`:''}
          <button class="btn btn-sm btn-outline" onclick="prefillUpload('${a.patientAbhaId}')">Upload</button>
        </td>
      </tr>`).join('')
    : '<tr><td colspan="7" style="padding:18px;color:var(--text-soft)">No appointments yet.</td></tr>';
  } catch { tbody.innerHTML='<tr><td colspan="7" style="padding:18px;color:var(--coral)">Error.</td></tr>'; }
}
 
async function updateStatus(id, status) {
  try {
    const res  = await fetch(`${API}/doctor/appointments/${id}/status`,
      { method:'PATCH', headers: authHeaders(), body: JSON.stringify({ status }) });
    const data = await res.json();
    showToast(data.success ? `✅ Status → ${status}` : '❌ ' + data.message);
    if (data.success) loadDoctorAppointments();
  } catch { showToast('❌ Error.'); }
}
 
async function loadDoctorPatients() {
  if (!authToken) return;
  const tbody = document.getElementById('patient-rows');
  if (!tbody) return;
  tbody.innerHTML = '<tr><td colspan="6" style="padding:18px;color:var(--text-soft)">⏳ Loading…</td></tr>';
  try {
    const res  = await fetch(`${API}/doctor/records`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) return;
    const map  = {};
    data.data.forEach(r => {
      if (!map[r.patientAbhaId])
        map[r.patientAbhaId] = { name: r.patientName, abha: r.patientAbhaId, records: 0, last: r.uploadDate };
      map[r.patientAbhaId].records++;
    });
    const list = Object.values(map);
    const clrs = ['#ede9ff','#ffe5e2','#e3f3ff','#e8f8f3','#fff3e0'];
    // Update subtitle with real count
  const sub = document.getElementById('d-patients-subtitle');
  if (sub) sub.textContent = list.length + ' patient(s) with uploaded records';
 
  tbody.innerHTML = list.length ? list.map((p,i) => `
      <tr>
        <td><div class="patient-cell"><div class="pat-av" style="background:${clrs[i%clrs.length]}">${p.name.charAt(0)}</div>${p.name}</div></td>
        <td style="font-size:0.78rem;color:var(--text-soft);font-weight:700">${p.abha}</td>
        <td>—</td><td>${formatDate(p.last)}</td>
        <td><span class="badge badge-blue">${p.records} files</span></td>
        <td>
          <button class="btn btn-sm btn-outline" style="margin-right:6px"
            onclick="viewPatientRecords('${p.abha}','${p.name}')">View</button>
          <button class="btn btn-sm btn-primary" onclick="prefillUpload('${p.abha}')">Upload</button>
        </td>
      </tr>`).join('')
    : '<tr><td colspan="6" style="padding:18px;color:var(--text-soft)">No patient records uploaded yet.</td></tr>';
  } catch { tbody.innerHTML='<tr><td colspan="6" style="padding:18px;color:var(--coral)">Error.</td></tr>'; }
}
 
async function viewPatientRecords(abhaId, patientName) {
  try {
    const res  = await fetch(`${API}/doctor/records/patient/${abhaId}`, { headers: authHeaders() });
    const data = await res.json();
    if (!data.success) { showToast('❌ ' + data.message); return; }
    const icons= { BLOOD_TEST:'🩸',PRESCRIPTION:'💊',ECG:'🫀',XRAY_RADIOLOGY:'🦴',
                   DIAGNOSIS_REPORT:'📋',DISCHARGE_SUMMARY:'🏥',MRI:'🧠',THYROID_PANEL:'🔬',OTHER:'📄' };
    const rows = data.data.map(r => `
      <div class="rec-item">
        <div class="rec-icon-box fi-teal">${icons[r.recordType]||'📄'}</div>
        <div class="rec-info"><div class="rn">${r.recordType.replace(/_/g,' ')}</div>
          <div class="rs">${formatDate(r.uploadDate)}</div>
          ${r.diagnosis?`<div class="rs">🩺 Dx: ${r.diagnosis}</div>`:''}
          ${r.prescription?`<div class="rs">💊 Rx: ${r.prescription}</div>`:''}
          ${r.notes?`<div class="rs">📝 ${r.notes}</div>`:''}
        </div>
        ${r.downloadUrl?`<button class="rec-download" onclick="downloadRecord('${r.downloadUrl}','${r.reportFileName}')">↓ PDF</button>`:''}
      </div>`).join('') || '<p style="color:var(--text-soft)">No records.</p>';
 
    document.getElementById('records-overlay')?.remove();
    document.body.insertAdjacentHTML('beforeend', `
      <div id="records-overlay" style="position:fixed;inset:0;background:rgba(0,0,0,0.4);z-index:600;
        display:flex;align-items:center;justify-content:center;backdrop-filter:blur(4px)">
        <div style="background:white;border-radius:20px;padding:32px;width:520px;max-width:95vw;
          max-height:80vh;overflow-y:auto;box-shadow:0 20px 60px rgba(0,0,0,0.18)">
          <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:20px">
            <h3 style="font-size:1.1rem;font-weight:900">📋 Records — ${patientName}</h3>
            <button onclick="document.getElementById('records-overlay').remove()"
              style="background:var(--bg);border:none;border-radius:50%;width:32px;height:32px;cursor:pointer">✕</button>
          </div>${rows}
        </div>
      </div>`);
  } catch { showToast('❌ Error loading records.'); }
}
 
 
/* ── File Select Handler ── */
function handleFileSelect(input) {
  const file  = input.files[0];
  const label = document.getElementById('upload-file-label');
  if (file && label) {
    label.textContent = '✅ ' + file.name + ' (' + (file.size / 1024).toFixed(1) + ' KB)';
    label.style.color = 'var(--teal-dark)';
    label.style.fontWeight = '800';
  }
}
 
/* ── Upload Report ─────────────────────────────── */
function prefillUpload(abhaId) {
  const btn = document.querySelectorAll('#page-doctor .sidebar-link')[3];
  switchTab('d', 'upload', btn);
  setTimeout(() => {
    const el = document.getElementById('upload-abha-id');
    if (el) { el.value = abhaId; showToast('✅ Patient ABHA ID pre-filled!'); }
  }, 100);
}
 
async function submitUploadReport() {
  if (!authToken) { showToast('❌ Please login first.'); return; }
 
  const abhaId       = (document.getElementById('upload-abha-id')?.value       || '').trim();
  const recordType   =  document.getElementById('upload-record-type')?.value    || '';
  const diagnosis    = (document.getElementById('upload-diagnosis')?.value      || '').trim();
  const prescription = (document.getElementById('upload-prescription')?.value   || '').trim();
  const notes        = (document.getElementById('upload-notes')?.value          || '').trim();
  const fileInput    =  document.getElementById('upload-file');
  const file         =  fileInput?.files?.[0];
 
  if (!abhaId)     { showToast('❌ Patient ABHA ID is required.'); return; }
  if (!recordType) { showToast('❌ Please select a report type.');  return; }
 
  try {
    showToast('⏳ Uploading report…');
    const fd = new FormData();
    fd.append('data', new Blob(
      [JSON.stringify({ patientAbhaId: abhaId, recordType, diagnosis, prescription, notes })],
      { type: 'application/json' }
    ));
    if (file) fd.append('file', file);
 
    const res  = await fetch(`${API}/doctor/records`, {
      method: 'POST',
      headers: { 'Authorization': 'Bearer ' + authToken },
      body: fd
    });
    const data = await res.json();
 
    if (data.success) {
      showToast('✅ Report uploaded & linked to patient ABHA ID!');
      ['upload-abha-id','upload-diagnosis','upload-prescription','upload-notes']
        .forEach(id => { const el = document.getElementById(id); if (el) el.value=''; });
      const rt = document.getElementById('upload-record-type');
      if (rt) rt.value = '';
      if (fileInput) fileInput.value = '';
      loadDoctorDashboard();
    } else {
      showToast('❌ ' + data.message);
    }
  } catch (e) {
    showToast('❌ Upload failed. Make sure patient ABHA ID is registered.');
  }
}
 
function downloadRecord(url, filename) {
  const a = document.createElement('a');
  a.href = 'http://localhost:8080' + url;
  a.download = filename || 'report';
  a.target = '_blank';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}
 
/* ──────────────────────────────────────
   INIT
────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
  loadSession();
  if (authToken && currentUser) {
    if (currentUser.role === 'PATIENT') {
      loadPatientProfile();
    } else if (currentUser.role === 'DOCTOR') {
      loadDoctorProfile();
    }
  }
});