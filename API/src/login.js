const https = require('https');
const querystring = require('querystring');

const login = function (util, param) {
    const url = new URL('https://aps.ncue.edu.tw/app/sess_student.php');

    const options = {
        hostname: url.hostname,
        port: 443,
        path: url.pathname,
        method: 'POST',
        headers: {
            'content-type': 'application/x-www-form-urlencoded'
        }
    }

    const postData = querystring.stringify({ 'p_usr': param.p_usr, 'p_pwd': param.p_pwd });

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
        req.write(postData)
        req.end();
    });


};

module.exports = login;