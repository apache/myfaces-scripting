/**
 * A simple hello world class for the embedded javascript engine
 */
var TestBean = function() {
    this._hello = "hello world from javascript";
}


TestBean.prototype.getHello = function() {
    return this._hello;
};


TestBean.prototype.setHello = function(hello) {
    this._hello = hello;
}


