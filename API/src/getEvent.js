const https = require('https');
const HTMLParser = require('node-html-parser');

function parseList(html) {
    const result = [];
    const root = HTMLParser.parse(html);
    root.querySelectorAll("#signup-table tbody tr").forEach(function (element) {
        const event = {};
        event.title = element.querySelectorAll("td")[0].querySelector("a").innerText;
        event.date = element.querySelectorAll("td")[1].innerText;
        event.limit = parseInt(element.querySelectorAll("td")[2].innerText);
        event.number = parseInt(element.querySelectorAll("td")[3].innerText);
        event.object = element.querySelectorAll("td")[4].innerText;
        event.status = element.querySelectorAll("td")[5].innerText;
        event.content = "https://aps.ncue.edu.tw/app/" + element.querySelectorAll("td")[0].querySelector("a").getAttribute("href");
        result.push(event);

    });

    return result;
}

function httpRequest(util, params) {
    const url = new URL(util.url);
    const options = {
        hostname: url.hostname,
        port: 443,
        path: url.pathname,
        method: util.method,
        headers: {
            'Cookie': util.cookies
        }
    }

    return new Promise(function (resolve, reject) {
        callback = function (response) {
            var data = ''
            response.on('data', function (chunk) {
                data += chunk;
            });

            response.on('end', function () {
                const parseResult = data.match(/(?<=alert\(").*(?=")/);
                const result = new Object;
                result.statusCode = response.statusCode;
                result.headers = response.headers;
                if (parseResult === null)
                    result.data = data;
                else
                    result.data = { message: parseResult[0] };
                if (parseResult === null && result.statusCode.toString().match(/2\d{2}|3\d{2}/))
                    resolve(result);
                else
                    reject(result);

            });


        }

        const req = https.request(options, callback)
            .on('error', (e) => {
                if (e.data === undefined)
                    e.data = e;
                if (e.statusCode === undefined)
                    e.statusCode = 500;
                reject(e);
            });
        req.end();
    });
}

const getEvent = function (util, param) {

    return new Promise(function (resolve, reject) {
        const returnObj = [];
        util.url = "https://aps.ncue.edu.tw/app/signup.php?selpp=1";
        util.method = "GET";
        httpRequest(util, param)
            .then(function (result) {
                returnObj.push({ type: "通識護照", events: parseList(result.data) });
                util.url = "https://aps.ncue.edu.tw/app/signup.php?selpp=2";
                util.method = "GET";
                return httpRequest(util, param);
            })
            .then(function (result) {
                returnObj.push({ type: "心靈成長護照", events: parseList(result.data) });
                util.url = "https://aps.ncue.edu.tw/app/signup.php?selpp=4";
                util.method = "GET";
                return httpRequest(util, param);
            })
            .then(function (result) {
                returnObj.push({ type: "語文護照", events: parseList(result.data) });
                util.url = "https://aps.ncue.edu.tw/app/signup.php?selpp=5";
                util.method = "GET";
                return httpRequest(util, param);
            })
            .then(function (result) {
                returnObj.push({ type: "多元學習課程", events: parseList(result.data) });
                result.data = returnObj;
                resolve(result);
            }).catch((err) => {
                if (err.data === undefined)
                    err.data = err;
                if (err.statusCode === undefined)
                    err.statusCode = 500;
                reject(err);
            });
    });

};

module.exports = getEvent;