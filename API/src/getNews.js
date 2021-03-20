const https = require('https');
const HTMLParser = require('node-html-parser');

function parseNews(html) {
    const result = [];
    const root = HTMLParser.parse(html);
    root.querySelectorAll("ul[data-role='listview'] li a").forEach(function (element) {
        const news = {};
        news.title = element.querySelector("h3").innerText;
        news.announcer = element.querySelector('p').innerText;
        news.date = element.querySelector('span').innerText;
        news.content = "https://aps.ncue.edu.tw/app/" + element.getAttribute('href');
        result.push(news);

    });

    return result;
}

const getNews = function (util, param) {
    const url = new URL('https://aps.ncue.edu.tw/app/news1.php');
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
                    result.data = parseNews(data);
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

module.exports = getNews;