<#import "template.ftl" as layout>

<@layout.registrationLayout displayInfo=true displayMessage=!messagesPerField.existsError('username'); section>

    <#if section = "header">
      Quên mật khẩu

    <#elseif section = "form">
      <form action="${url.loginAction}" method="post">

        <p style="color:#64748b; font-size:14px; margin-bottom:20px; text-align:center; line-height:1.6;">
          Nhập email của bạn, chúng tôi sẽ gửi link đặt lại mật khẩu.
        </p>

        <div class="form-group">
          <label class="form-label" for="username">
              <#if !realm.loginWithEmailAllowed>
                  ${msg("username")}
              <#elseif !realm.registrationEmailAsUsername>
                  ${msg("usernameOrEmail")}
              <#else>
                Email
              </#if>
          </label>
          <input
              class="form-control"
              type="text"
              id="username"
              name="username"
              autofocus
              placeholder="email@example.com"
              value="${(auth.attemptedUsername!'')}"
          />
            <#if messagesPerField.existsError('username')>
              <span style="color:#ef4444; font-size:12px; margin-top:4px; display:block;">
                        ${kcSanitize(messagesPerField.get('username'))?no_esc}
                    </span>
            </#if>
        </div>

        <div style="display:flex; gap:10px; margin-top:8px;">
          <a href="${url.loginUrl}" style="
                    flex:1; display:flex; align-items:center; justify-content:center;
                    padding:10px; border:1.5px solid #e2e8f0; border-radius:10px;
                    color:#64748b; font-size:14px; font-weight:600;
                    text-decoration:none; font-family:inherit;">
            ← Quay lại
          </a>
          <input type="submit" value="Gửi email" style="flex:2;"/>
        </div>

      </form>

    <#elseif section = "info">
      <p style="color:#64748b; font-size:13px; text-align:center; margin-top:16px;">
          ${msg("emailInstruction")}
      </p>
    </#if>

</@layout.registrationLayout>