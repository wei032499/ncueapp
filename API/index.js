const express = require('express');
const app = express();
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const ncueAPI = require('./api');

app.use(cookieParser('szRuAWci3M9KbKB'));
app.use(bodyParser.urlencoded({ extended: false }));


/**
 * router
 */
app.get('/', function (req, res) {
    res.sendFile('login.html', { root: __dirname });
});

app.get('/news', function (req, res) {
    const util = { cookies: cookieSerialize(req.signedCookies) };
    const param = req.body;
    ncueAPI.getNews(util, param)
        .then(function (result) {
            setCookieByHeaders(res, result.headers);
            res.status(result.statusCode).send(result.data);
        }).catch((err) => {
            res.status(err.statusCode).send(err.data);
        });

});
app.post('/login', function (req, res) {
    const util = { cookies: cookieSerialize(req.signedCookies) };
    const param = req.body;
    ncueAPI.login(util, param)
        .then(function (result) {
            setCookieByHeaders(res, result.headers);
            res.status(result.statusCode).send(result.data);
        }).catch((err) => {
            res.status(err.statusCode).send(err.data);
        });

});
app.get('/score', function (req, res) {
    const util = { cookies: cookieSerialize(req.signedCookies) };
    const param = req.body;
    ncueAPI.getScore(util, param)
        .then(function (result) {
            setCookieByHeaders(res, result.headers);
            res.status(result.statusCode).send(result.data);
        }).catch((err) => {
            res.status(err.statusCode).send(err.data);
        });

});

app.get('/event/list', function (req, res) {
    const util = { cookies: cookieSerialize(req.signedCookies) };
    const param = req.body;
    ncueAPI.getEvent(util, param)
        .then(function (result) {
            setCookieByHeaders(res, result.headers);
            res.status(result.statusCode).send(result.data);
        }).catch((err) => {
            res.status(err.statusCode).send(err.data);
        });

});
app.get('/event/me', function (req, res) {
    const util = { cookies: cookieSerialize(req.signedCookies) };
    const param = req.body;
    ncueAPI.getRecognized(util, param)
        .then(function (result) {
            setCookieByHeaders(res, result.headers);
            res.status(result.statusCode).send(result.data);
        }).catch((err) => {
            res.status(err.statusCode).send(err.data);
        });

});

app.get('/logout', function (req, res) {
    if (typeof req.signedCookies === 'object') {
        for (const [key, value] of Object.entries(req.signedCookies)) {
            if (key !== "")
                res.clearCookie(key);
        }
    }
    res.status(200).send({});
});



/**
 * listener
 */
const port = 1130;
app.listen(port, () => {
    console.log(`App listening at http://localhost:${port}`)
})



/**
 * functions
 */

/**
 * set cookies to response from http header
 * @param {Response<any, Record<string, any>, number>} res Response
 * @param {object} headers HTTP header
 */
function setCookieByHeaders(res, headers) {
    if (headers['set-cookie'] !== undefined)
        headers['set-cookie'].forEach(function (cookie) {
            const cookie_s = cookie.split(';');
            const options = { signed: true };
            const [key, value] = cookie_s[0].split('=');
            if (value !== undefined)
                res.cookie(key, value, options);
        });
}

/**
 * convert cookie from object to string
 * @param {object} cookieObj cookie object
 * @returns {string} cookie string (key=value)
 */
function cookieSerialize(cookieObj) {
    var cookies = "";
    if (typeof cookieObj === 'object') {
        for (const [key, value] of Object.entries(cookieObj)) {
            cookies += key + "=" + value + ";";
        }
    }
    return cookies;
}