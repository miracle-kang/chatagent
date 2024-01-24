@${user.username}，您好

<#if user.password?has_content>
恭喜您成功注册春蚕AI账户！为了确保您的账户安全，请妥善保管您的账户信息。以下是您的登录凭据：

用户名：${user.username}
密码： ${user.password}

请妥善保管这些信息，并定期更改密码以确保安全。
<#else>
恭喜您成功注册春蚕AI账户！
</#if>

如果您有任何问题，请随时与我们的支持团队联系。再次感谢您选择春蚕AI，并期待为您提供卓越的服务。

春蚕AI团队敬上
