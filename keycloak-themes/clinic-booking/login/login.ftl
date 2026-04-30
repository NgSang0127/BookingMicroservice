<#import "template.ftl" as layout>

<@layout.registrationLayout
  displayInfo=social.displayInfo
  displayWide=(realm.password && social.providers??); section>

  <#-- HEADER section -->
<#if section = "header">
    ${msg("loginAccountTitle")}

  <#-- FORM section -->
  <#elseif section = "form">
    <form id="kc-form-login"
          onsubmit="login.disabled = true; return true;"
          action="${url.loginAction}"
          method="post">

      <#if !usernameHidden??>
        <div class="form-group">
          <label class="form-label" for="username">
            <#if !realm.loginWithEmailAllowed>
              ${msg("username")}
            <#elseif !realm.registrationEmailAsUsername>
              ${msg("usernameOrEmail")}
            <#else>
              ${msg("email")}
            </#if>
          </label>
          <input
            class="form-control"
            tabindex="1"
            id="username"
            name="username"
            value="${login.username!''}"
            type="text"
            autofocus
            autocomplete="username"
            placeholder="<#if realm.loginWithEmailAllowed>email@example.com<#else>Tên đăng nhập</#if>"
          />
          <#if messagesPerField.existsError('username','password')>
            <span style="color:#ef4444;font-size:12px;margin-top:4px;display:block;">
              ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
            </span>
          </#if>
        </div>
      </#if>

      <div class="form-group">
        <label class="form-label" for="password">${msg("password")}</label>
        <div class="password-wrapper"> <#-- Thêm class container -->
          <input class="form-control" tabindex="2" id="password" name="password"
                 type="password" autocomplete="current-password" placeholder="••••••••" />

            <#-- Nút mắt ẩn/hiện mật khẩu (cần thêm CSS để căn chỉnh) -->
          <span class="toggle-password" id="togglePassword">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
        </span>
        </div>
      </div>
      <script>
        document.addEventListener('DOMContentLoaded', function () {
          const passwordInput = document.querySelector('#password');
          const togglePassword = document.querySelector('#togglePassword');
          const eyeIcon = document.querySelector('#eyeIcon');

          togglePassword.addEventListener('click', function () {
            // Chuyển đổi type
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            if (type === 'text') {
              eyeIcon.innerHTML = `
          <path stroke-linecap="round" stroke-linejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.542-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l18 18" />
        `;
            } else {
              eyeIcon.innerHTML = `
          <path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
        `;
            }
          });
        });
      </script>

      <div class="form-options">
        <#if realm.rememberMe && !usernameEditDisabled??>
          <label>
            <input tabindex="3" type="checkbox" name="rememberMe"
                   <#if login.rememberMe??>checked</#if>/>
            ${msg("rememberMe")}
          </label>
        <#else>
          <span></span>
        </#if>
        <#if realm.resetPasswordAllowed>
          <a tabindex="5" href="${url.loginResetCredentialsUrl}">
            ${msg("doForgotPassword")}
          </a>
        </#if>
      </div>

      <div id="kc-form-buttons">
        <input tabindex="4" type="submit" id="kc-login" value="${msg("doLogIn")}"/>
      </div>

    </form>

    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
      <div class="register-link">
        ${msg("noAccount")}
        <a tabindex="6" href="http://localhost:5173/register">${msg("doRegister")}</a>
      </div>
    </#if>

  <#-- SOCIAL PROVIDERS section -->
  <#elseif section = "socialProviders">
    <#if realm.password && social.providers?? && social.providers?has_content>
      <div id="kc-social-providers">
        <div class="social-divider">
          <span>${msg("identity-provider-login-label")}</span>
        </div>
        <ul class="social-links">
          <#list social.providers as p>
            <li>
              <a id="social-${p.alias}" href="${p.loginUrl}">
                <#if p.iconClasses?has_content>
                  <i class="${p.iconClasses!}" aria-hidden="true"></i>
                </#if>
                ${p.displayName!}
              </a>
            </li>
          </#list>
        </ul>
      </div>
    </#if>

  <#-- INFO section -->
  <#elseif section = "info">
    <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
      <span>${msg("noAccount")} <a href="http://localhost:5173/register">${msg("doRegister")}</a></span>
    </#if>
  </#if>

</@layout.registrationLayout>
