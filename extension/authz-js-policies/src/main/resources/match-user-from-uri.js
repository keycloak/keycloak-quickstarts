var context = $evaluation.getContext();
var attributes = context.getAttributes();
var httpUri = attributes.getValue('http.uri');

if (httpUri) {
    var uriParts = httpUri.asString(0).split('/');
    var identity = context.getIdentity();
    var username = identity.getAttributes().getValue('preferred_username').asString(0);

    if (uriParts[2] == username) {
        $evaluation.grant();
    }
}