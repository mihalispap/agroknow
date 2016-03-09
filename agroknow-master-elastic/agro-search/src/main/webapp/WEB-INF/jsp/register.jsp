<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head profile="http://gmpg.org/xfn/11">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>AgroKnow API &raquo; Registration</title>
        <meta name="viewport" content="width=device-width">
        <style type="text/css">
            .codeblock {font-size: 14px; line-height: 13px; border: 1px solid #999; background: #EEE; padding: 10px; border-radius: 3px;}
        </style>
    </head>
    <body>
        <div id="viewport" style="width:960px;margin:0 auto;">
            <h1>Agroknow Search API - Registration</h1>

            <p><a name="api-keys"></a></p>
            <h3>API Keys</h3>
            <p>In order to access the AgroKnow Search API, you must first request an API key. The key is a 32-character
                string that uniquely identifies you to the AgroKnow Search API servers. <a name="get-a-key"></a><br></p>

            <h4>How to get one</h4>
            <p>You have two ways to request an API key. The first is to fill the form below and submit it.</p>

            <form method="POST" action="/register">
                <label for="email">E-mail Address</label>
                <input id="email" name="email" type="email" required />

                <button id="registerBtn" type="submit">Register</button>
            </form>
            <br/>
            <c:choose>
                <c:when test="${status == 'SUCCESS'}">
                    <span style="color:green">${message}</span>
                </c:when>
                <c:when test="${status == 'FAILURE'}">
                    <span style="color:red">${message}</span>
                </c:when>
                <c:when test="${status == 'INVALID'}">
                    <span style="color:orangered">${email}</span>
                </c:when>
            </c:choose>

            <p>The second is to send a HTTP <code>POST</code> request to the following URL:
            <pre style="padding-left: 30px;">http://&lt;DOMAIN&gt;/register?email=YOUR_EMAIL@example.com</pre>

            <p>
                where <code>YOUR_EMAIL@example.com</code> is the e-mail address with which you wish
                to associate your or your organization’s API usage.
            </p>

            <p>
                You can send a <code>POST</code> request of this sort using <code>curl</code>, a free and
                open-source utility installed by default in all modern versions of Linux, BSD, and Mac OS X.
                You may use the following command:
            </p>
            <pre class="codeblock">
curl -v -XPOST http://&lt;DOMAIN&gt;/register?email=YOUR_EMAIL@example.com
            </pre>

            <p>You will receive the following output:</p>
            <pre class="codeblock">
&lt; HTTP/1.1 201 Created
&lt; Server: Apache
&lt; Content-Type: application/json
&lt; Transfer-Encoding: chunked
&lt; Vary: Accept-Encoding
&lt; Date: Wed, XX Jan XXXX XX:XX:XX GMT
&lt; ...
&lt;
{
  "message" : "Registration completed successfully. Please check your email for instructions."
}
            </pre>

            <p>
                The API key will then be sent to the e-mail address you provided. To successfully query the API,
                you <strong>must</strong> include the <code>?api_key=</code> parameter with the 32-character hash following.
                If you won't/can't use the request parameter (api_key) to add the key to the request you can use the
                HTTP <code>Authorization</code> header with a value of <code>AGRO &lt;key&gt;</code> like in the following
                example:
            </p>
            <pre class="codeblock">
curl -v -XGET 'http://&lt;DOMAIN&gt;/v1/akif?q=*&page_size=0' -H 'Authorization: AGRO &lt;key&gt;'
            </pre>
            <pre class="codeblock">
> GET /v1/akif?q=*&facets=creationDate;year&page_size=0 HTTP/1.1
> User-Agent: curl/7.34.0
> Host: &lt;DOMAIN&gt;
> Accept: */*
> Authorization: AGRO &lt;key&gt;
>
            </pre>
            <pre class="codeblock">
< HTTP/1.1 200 OK
< Server: Apache
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Vary: Accept-Encoding
< Date: Wed, XX Jan XXXX XX:XX:XX GMT
<
{
  "total" : 15061,
  "time" : 66,
  "page" : 1,
  "pageSize" : 0,
  "sortOrder" : "asc",
  "results" : [ ]
}
            </pre>

        </div>
    </body>
</html>