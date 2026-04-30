<#import "footer.ftl" as loginFooter>

<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false displayWide=false>
<!DOCTYPE html>
<html class="${properties.kcHtmlClass!}" lang="${locale.currentLanguageTag!'en'}">

<head>
  <meta charset="utf-8">
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

  <#if properties.meta?has_content>
    <#list properties.meta?split(' ') as meta>
      <meta name="${meta?split('==')[0]}" content="${meta?split('==')[1]}"/>
    </#list>
  </#if>

  <title>${msg("loginTitle",(realm.displayName!''))}</title>
  <link rel="icon" href="${url.resourcesPath}/img/favicon.ico" type="image/x-icon"/>

  <link rel="preconnect" href="https://fonts.googleapis.com"/>
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
  <link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:opsz,wght@9..40,300;9..40,400;9..40,500;9..40,600&display=swap" rel="stylesheet"/>

  <#if properties.stylesCommon?has_content>
    <#list properties.stylesCommon?split(' ') as style>
      <link href="${url.resourcesCommonPath}/${style}" rel="stylesheet"/>
    </#list>
  </#if>
  <#if properties.styles?has_content>
    <#list properties.styles?split(' ') as style>
      <link href="${url.resourcesPath}/${style}" rel="stylesheet"/>
    </#list>
  </#if>
  <#if properties.scripts?has_content>
    <#list properties.scripts?split(' ') as script>
      <script src="${url.resourcesPath}/${script}" type="text/javascript"></script>
    </#list>
  </#if>
  <#if scripts??>
    <#list scripts as script>
      <script src="${script}" type="text/javascript"></script>
    </#list>
  </#if>

  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

    :root {
      --brand-500: #0066cc;
      --brand-600: #0052a3;
      --n50:  #f8faff;
      --n100: #f0f4ff;
      --n200: #e2e8f0;
      --n300: #cbd5e1;
      --n400: #94a3b8;
      --n500: #64748b;
      --n700: #334155;
      --n800: #1e293b;
      --n900: #0f172a;
      --r-md: 8px; --r-lg: 16px;
      --shadow: 0 4px 6px -1px rgba(0,0,0,.07);
      --focus: 0 0 0 3px rgba(0,102,204,.2);
    }

    html, body {
      height: 100%;
      font-family: 'DM Sans', sans-serif;
      color: var(--n800);
      -webkit-font-smoothing: antialiased;
    }

    /* ── Background xanh gradient như ảnh ── */
    body {
      min-height: 100vh !important;
      /* Dải màu xanh từ Navy đậm sang Cyan sáng */
      background: #4284DB;  /* fallback for old browsers */
      background: -webkit-linear-gradient(to right, #29EAC4, #4284DB) !important;  /* Chrome 10-25, Safari 5.1-6 */
      background: linear-gradient(to right, #29EAC4, #4284DB) !important; /* W3C, IE 10+/ Edge, Firefox 16+, Chrome 26+, Opera
      12+, Safari 7+ */


      display: flex !important;
      flex-direction: column !important;
      align-items: center !important;
      justify-content: center !important;
      padding: 24px 16px !important;
      margin: 0 !important;
    }

    /* Xóa bỏ mọi lớp phủ mờ có thể có từ theme mặc định */
    body::before, body::after {
      display: none !important;
    }

    /* ── Outer wrapper ── */
    #kc-outer-wrapper {
      position: relative;
      z-index: 1;
      width: 100%;
      max-width: 400px;
    }

    /* ── Card trắng ── */
    .kc-card {
      background: #ffffff;
      border-radius: 16px;
      padding: 32px 32px 28px;
      box-shadow: 0 24px 64px rgba(0,0,0,0.18);
      border: none;
      animation: up .35s cubic-bezier(.16,1,.3,1) both;
    }
    @keyframes up {
      from { opacity: 0; transform: translateY(16px); }
      to   { opacity: 1; transform: translateY(0); }
    }

    /* ── Title ── */
    #kc-page-title {
      font-family: 'DM Sans', sans-serif;
      font-size: 20px;
      font-weight: 600;
      color: #1e293b;
      text-align: left;
      margin-bottom: 20px;
      line-height: 1.3;
    }

    /* ── Labels ── */
    .form-group { margin-bottom: 16px; }
    .form-label {
      display: block;
      font-size: 13px;
      font-weight: 500;
      color: var(--n700);
      margin-bottom: 5px;
    }

    /* ── Inputs ── */
    .form-control {
      width: 100%;
      padding: 9px 12px;
      font-family: inherit;
      font-size: 14px;
      color: var(--n800);
      background: #ffffff;
      border: 1.5px solid #d1d9e6;
      border-radius: var(--r-md);
      outline: none;
      transition: border-color .2s, box-shadow .2s;
      -webkit-appearance: none;
    }
    .form-control:focus {
      border-color: #0066cc;
      box-shadow: var(--focus);
    }
    .form-control::placeholder { color: var(--n400); }

    /* ── Remember me + Forgot ── */
    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 18px;
    }
    .form-options label {
      display: flex; align-items: center; gap: 6px;
      font-size: 13px; color: var(--n500); cursor: pointer;
    }
    .form-options input[type="checkbox"] {
      width: 14px; height: 14px; accent-color: var(--brand-500);
    }
    .form-options a {
      font-size: 13px; font-weight: 500;
      color: var(--brand-500); text-decoration: none;
    }
    .form-options a:hover { text-decoration: underline; }

    /* ── Submit button — bo tròn như ảnh ── */
    input[type="submit"] {
      width: 100%;
      padding: 11px;
      font-family: inherit;
      font-size: 15px;
      font-weight: 600;
      color: #fff;
      background: #1a5fd4;
      border: none;
      border-radius: 50px;
      cursor: pointer;
      box-shadow: 0 2px 12px rgba(26,95,212,.35);
      transition: background .2s, transform .15s;
    }
    input[type="submit"]:hover {
      background: #1450bb;
      transform: translateY(-1px);
    }
    input[type="submit"]:active { transform: translateY(0); }

    /* ── Register link ── */
    .register-link {
      margin-top: 18px;
      text-align: center;
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;
      font-size: 13px;
      color: var(--n500);
    }
    .register-link a {
      font-weight: 600;
      color: var(--brand-500);
      text-decoration: none;
      margin-left: 3px;
    }
    .register-link a:hover { text-decoration: underline; }

    /* ── Feedback alerts ── */
    .kc-feedback-text {
      display: flex; align-items: center; gap: 8px;
      padding: 10px 13px; border-radius: var(--r-md);
      margin-bottom: 16px; font-size: 13px; font-weight: 500;
    }
    .alert-error   { background: #fef2f2; border: 1px solid #fecaca; color: #b91c1c; }
    .alert-warning { background: #fffbeb; border: 1px solid #fde68a; color: #92400e; }
    .alert-success { background: #f0fdf4; border: 1px solid #bbf7d0; color: #15803d; }
    .alert-info    { background: #eff6ff; border: 1px solid #bfdbfe; color: #1d4ed8; }

    /* ── Footer ── */
    #kc-footer {
      margin-top: 20px; text-align: center;
      font-size: 12px; color: rgba(255,255,255,0.5);
    }

    /* ── Misc ── */
    #kc-username {
      display: flex; justify-content: space-between; align-items: center;
      padding: 9px 13px; background: var(--n100); border: 1px solid var(--n200);
      border-radius: var(--r-md); margin-bottom: 16px;
    }
    #kc-attempted-username { font-size: 14px; font-weight: 500; }
    #reset-login { font-size: 12px; color: var(--brand-500); text-decoration: none; }
    #try-another-way { display: block; text-align: center; margin-top: 12px; font-size: 13px; color: var(--brand-500); }
    #kc-info-wrapper { text-align: center; font-size: 13px; color: var(--n500); margin-top: 14px; }
    #kc-info-wrapper a { color: var(--brand-500); font-weight: 500; }
    .required { color: #ef4444; }
    .subtitle { font-size: 13px; color: var(--n500); margin-bottom: 8px; }

    @media (max-width: 480px) {
      .kc-card { padding: 24px 20px 20px; }
      #kc-outer-wrapper { max-width: 100%; }
    }
  </style>
</head>

<body>
<div id="kc-outer-wrapper">

    <#-- ✅ LOGO + TÊN NẰM NGOÀI CARD — trên background xanh -->
    <#-- Tìm đoạn LOGO AREA và cập nhật lại stroke/fill -->
  <div id="kc-logo-area" style="text-align:center; margin-bottom:24px;">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="50 30 300 300"
         width="56" height="56" style="display:inline-block; margin-bottom:6px;">
      <path d="M 120 230 L 70 230 L 70 130 L 150 130 L 150 50 L 250 50 L 250 130 L 330 130 L 330 230 L 250 230 L 250 310 L 150 310 L 150 260"
            fill="none" stroke="#0088ce" stroke-width="16" /> <#-- Đổi stroke thành xanh -->
      <path d="M 115 238 C 130 140, 190 125, 260 100 C 245 170, 200 215, 150 235 C 185 215, 230 170, 240 125 C 180 140, 130 170, 125 238 Z"
            fill="#00a68c" /> <#-- Đổi fill thành xanh lá -->
      <path d="M 142 265 C 145 185, 175 160, 230 155 C 190 170, 158 205, 158 265 Z"
            fill="#0088ce" /> <#-- Đổi fill thành xanh dương -->
    </svg>
      <#-- Tên App -->
    <div class="logo-text">
      Clinic Booking
    </div>
  </div>

    <#-- ✅ CARD -->
  <div class="kc-card">

      <#-- Bỏ hoàn toàn dropdown language -->

      <#if !(auth?has_content && auth.showUsername() && !auth.showResetCredentials())>
          <#if displayRequiredFields>
            <div class="subtitle"><span class="required">*</span> ${msg("requiredFields")}</div>
          </#if>
        <h1 id="kc-page-title"><#nested "header"></h1>
      <#else>
          <#nested "show-username">
        <div id="kc-username">
          <label id="kc-attempted-username">${auth.attemptedUsername}</label>
          <a id="reset-login" href="${url.loginRestartFlowUrl}">${msg("restartLoginTooltip")}</a>
        </div>
      </#if>

      <#if displayMessage && message?has_content && (message.type != 'warning' || !isAppInitiatedAction??)>
        <div class="kc-feedback-text alert-${message.type}">
            <#if message.type = 'success'>✓</#if>
            <#if message.type = 'warning'>⚠</#if>
            <#if message.type = 'error'>✕</#if>
            <#if message.type = 'info'>ℹ</#if>
          <span>${kcSanitize(message.summary)?no_esc}</span>
        </div>
      </#if>

      <#nested "form">

      <#if auth?has_content && auth.showTryAnotherWayLink()>
        <form id="kc-select-try-another-way-form" action="${url.loginAction}" method="post">
          <input type="hidden" name="tryAnotherWay" value="on"/>
          <a href="#" id="try-another-way"
             onclick="document.forms['kc-select-try-another-way-form'].requestSubmit();return false;">
              ${msg("doTryAnotherWay")}
          </a>
        </form>
      </#if>

      <#if switchOrganizationEnabled?? && switchOrganizationEnabled>
        <form id="kc-switch-organization-form" action="${url.loginAction}" method="post">
          <input type="hidden" name="switchOrganization" value="true"/>
          <a href="#" id="switch-organization"
             onclick="document.forms['kc-switch-organization-form'].requestSubmit();return false;">
              ${msg("doSwitchOrganization")}
          </a>
        </form>
      </#if>

      <#nested "socialProviders">

      <#if displayInfo>
        <div id="kc-info">
          <div id="kc-info-wrapper">
              <#nested "info">
          </div>
        </div>
      </#if>

      <@loginFooter.content/>

  </div>
</div>
</body>
</html>
</#macro>
