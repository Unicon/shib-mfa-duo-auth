# Shibboleth Duo Security Authentication Module

DuoSecurity multifactor authentication plugin for the Shibboleth identity provider v3.

> This project is made public here on Github as part of Unicon's [Open Source Support program](https://unicon.net/opensource).
Professional Support / Integration Assistance for this module is available. For more information [visit](https://unicon.net/opensource/shibboleth).

## Features

* Allows the `http://www.duosecurity.com/` authnContext

## Installation

1. Obtain distribution either as a binary download or building from source
    * Binary Download

        Download from [https://github.com/Unicon/shib-mfa-duo-auth/releases](https://github.com/Unicon/shib-mfa-duo-auth/releases)

    * From Source

        ```
        git clone https://github.com/Unicon/shib-mfa-duo-auth.git
        cd shib-mfa-duo-auth
        ./gradlew clean distZip
        ```

        The distribution will be found at `build/distributions/shibboleth-duo-auth-{VERSION}.zip`
1. `unzip shibboleth-duo-auth*.zip`
1. copy the `edit-webapp`, `conf` and `views` directories from the distribution into `${idp.home}`; eg `cd shibboleth-duo-auth*; cp -R * ${idp.home}`
1. modify `${idp.home}/conf/duo.properties` for your Duo configuration
1. modify `${idp.home}/conf/idp.properties`. edit the following properties:
    * `idp.additionalProperties`: add `/conf/duo.properties`:

        ```
        idp.additionalProperties= /conf/ldap.properties, /conf/saml-nameid.properties, /conf/services.properties, /conf/duo.properties
        ```

    * `idp.authn.flows`: add `Duo`:

        ```
        idp.authn.flows= Password|Duo
        ```

    * `idp.authn.flows.initial`: set up an initial authentication flow. For instance, password:

        ```
        idp.authn.flows.initial = Password
        ```

1. modify `${idp.home}/edit-webapp/WEB-INF/web.xml`

    If you don't have this file, you can copy from `${idp.home}/webapp/WEB-INF/web.xml`

    * `contextConfigLocation`:

        ```
        <context-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>
                    classpath*:/META-INF/shibboleth-idp/conf/global.xml
                    ${idp.home}/system/conf/global-system.xml
                </param-value>
        </context-param>
        ```

    * `idp` servlet, `contextConfigLocation` init-param

        ```
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath*:/META-INF/shibboleth-idp/conf/webflow-config.xml ${idp.home}/system/conf/mvc-beans.xml ${idp.home}/system/conf/webflow-config.xml</param-value>
        </init-param>
        ```

1. rebuild the IdP war file

    ```
    cd ${idp.home}/bin
    ./build.sh
    ```
