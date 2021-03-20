const https = require('https');
const HTMLParser = require('node-html-parser');

function parseRecognized(html) {
    const result = [];
    const root = HTMLParser.parse(html);
    root.querySelectorAll("[data-role='listview'] li").forEach(function (element) {
        if (element.getAttribute('data-role') === "list-divider") {
            result.push({ type: element.innerText, events: [] });
        }
        else {
            const event = {};
            event.title = element.querySelector("a h3").innerText;
            event.hours = parseFloat(element.querySelector("a span").innerText);
            event.date = element.querySelector("a p").innerText.match(/(?<= ).*/)[0];
            event.content = "https://aps.ncue.edu.tw/app/" + element.querySelector("a").getAttribute("href");
            result[result.length - 1].events.push(event);

        }
    });

    return result;
}

const getRecognized = function (util, param) {
    const url = new URL('https://aps.ncue.edu.tw/app/hour.php');
    const options = {
        hostname: url.hostname,
        port: 443,
        path: url.pathname,
        method: 'GET',
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
                    result.data = parseRecognized(data);
                else
                    result.data = { message: parseResult[0] };
                if (result.statusCode.toString().match(/2\d{2}|3\d{2}/))
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


};

module.exports = getRecognized;