var context = $evaluation.context;

var attributes = context.attributes;

if (attributes.containsValue('some-claim', 'claim-value')) {
    $evaluation.grant();
}