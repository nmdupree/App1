function State(url, templateUrl, controller, controllerAs, factory, resolveProp){
    this.url = url
    this.templateUrl = templateUrl
    this.controller = controller
    this.controllerAs = controllerAs
    if(factory){
        this.resolve = {}
        this.resolve[resolveProp] = [factory, function(factory){
            return factory.getAll().catch(function(res){
                console.log('Error.  result is ', res);
                return {error: 'failed to get Data'}
            })
        }]
    }
}