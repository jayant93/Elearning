module.exports =
{
    development : {
        ENV_VARS: {
            loginUrl: "http://localhost:9090/um",
            apiUrl: "/um/api",
            appUrl: "/um/home",
            appSecret : "YjSMydMzRkNkxTWX6TzNbfTH5QVEsPgU",
            recaptchaPublic: "6LcTlGMUAAAAAFCgUYEuwTgjveRcEU-e69K8VSdr",
            tokenRefreshTimeOut: 300000
        }
    },
    test : {
        ENV_VARS: {
            loginUrl: "http://192.168.1.28:5050/um",
            apiUrl: "/um/api",
            appUrl: "/um/home",
            appSecret : "YjSMydMzRkNkxTWX6TzNbfTH5QVEsPgU",
            recaptchaPublic: "6LcTlGMUAAAAAFCgUYEuwTgjveRcEU-e69K8VSdr",
            tokenRefreshTimeOut: 300000
        }
    },
    production : {
        ENV_VARS: {
            loginUrl: "IP:PORT/um",
            apiUrl: "/um/api",
            appUrl: "/um/home",
            appSecret : "U7ygsSvYeey1Kq9No7HoV2W1IdoPZ3Ia",
            recaptchaPublic: "6LcTlGMUAAAAAFCgUYEuwTgjveRcEU-e69K8VSdr",
            tokenRefreshTimeOut: 300000
        }
    },
    production_dswd_cllrs : {
        ENV_VARS: {
            loginUrl: "https://cllrs.dswd.gov.ph/um",
            apiUrl: "/um/api",
            appUrl: "/um/home",
            appSecret : "U7ygsSvYeey1Kq9No7HoV2W1IdoPZ3Ia",
            recaptchaPublic: "6LcTlGMUAAAAAFCgUYEuwTgjveRcEU-e69K8VSdr",
            tokenRefreshTimeOut: 300000
        }
    },
};