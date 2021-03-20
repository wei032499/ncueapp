const funList = [
    'getDcard',
    'getNews',
    'getEvent',
    'getRecognized',
    'getScore',
    'login'
];

var api = {};
funList.map((text, index) => {
    api[text] = function (util, param) {
        return require('./src/' + text)(util, param);
    }
});

module.exports = api;