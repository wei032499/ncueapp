const https = require('https');
const HTMLParser = require('node-html-parser');

function parseScore(html) {
    const result = { score: [], threshold: [] };
    const root = HTMLParser.parse(html);
    root.querySelectorAll("li").forEach(function (element) {
        if (element.getAttribute('data-role') === "list-divider") {
            result.score.push({ semester: element.innerText, subjects: [] });
        }
        else {
            const match = element.innerText.match(/(\W+)分數：(\d+)　\((\W+)\)(\d+)/);
            if (match === null) {

                element.innerText.split(/[\s(&nbsp;)]+/)
                    .forEach(element => {
                        if (element !== '') {
                            const threshold = element.split('：');
                            result.threshold.push({ name: threshold[0], status: threshold[1] });
                        }
                    });


            }
            else {
                const subject = {};
                subject.name = match[1];
                subject.score = match[2];
                subject.type = match[3];
                subject.credits = match[4];
                result.score[result.score.length - 1].subjects.push(subject);
            }


        }
    });

    return result;
}

const getScore = function (util, param) {
    const url = new URL('https://aps.ncue.edu.tw/app/score.php');
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
                    result.data = parseScore(data);
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

module.exports = getScore;