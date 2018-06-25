var module = angular.module('photoz', ['ngRoute', 'ngResource']);

var resourceServerId = 'photoz-restful-api';
var apiUrl = window.location.origin + '/' + resourceServerId;

angular.element(document).ready(function ($http) {
    var keycloak = new Keycloak('keycloak.json');
    keycloak.init({onLoad: 'login-required'}).success(function () {
        console.log('User is now authenticated.');

        module.factory('Identity', function () {
            return new Identity(keycloak);
        });

        angular.bootstrap(document, ["photoz"]);
    }).error(function () {
        window.location.reload();
    });
});

module.config(function ($httpProvider, $routeProvider) {
    $httpProvider.interceptors.push('authInterceptor');
    $routeProvider.when('/', {
        templateUrl: 'partials/home.html',
        controller: 'GlobalCtrl'
    }).when('/album/create', {
        templateUrl: 'partials/album/create.html',
        controller: 'AlbumCtrl',
    }).when('/album/:id', {
        templateUrl: 'partials/album/detail.html',
        controller: 'AlbumCtrl',
    }).when('/admin/album', {
        templateUrl: 'partials/admin/albums.html',
        controller: 'AdminAlbumCtrl',
    }).when('/profile', {
        templateUrl: 'partials/profile.html',
        controller: 'ProfileCtrl',
    });
});

module.controller('GlobalCtrl', function ($scope, $http, $route, $location, Album, Identity) {
    Album.query(function (albums) {
        $scope.albums = albums;
    });
    Album.shares(function (albums) {
        $scope.shares = albums;
    });

    $scope.Identity = Identity;

    $scope.deleteAlbum = function (album) {
        new Album(album).$delete({id: album.id}, function () {
            $route.reload();
        });
    }
});

module.controller('TokenCtrl', function ($scope, Identity) {
    $scope.showAccessToken = function () {
        document.getElementById("output").innerHTML = JSON.stringify(jwt_decode(Identity.authc.token), null, '  ');
    }

    $scope.Identity = Identity;
});

module.controller('AlbumCtrl', function ($scope, $http, $routeParams, $location, Album) {
    $scope.album = {};
    if ($routeParams.id) {
        $scope.album = Album.get({id: $routeParams.id});
    }
    $scope.create = function () {
        var newAlbum = new Album($scope.album);
        newAlbum.$save({}, function (data) {
            $location.path('/');
        });
    };
    $scope.goto = function (path) {
        $location.path(path)
    }
});

module.controller('ProfileCtrl', function ($scope, $http, $routeParams, $location, Profile) {
    $scope.profile = Profile.get();
});

module.controller('AdminAlbumCtrl', function ($scope, $http, $route, $location, AdminAlbum, Album) {
    $scope.albums = {};
    $http.get(apiUrl + '/admin/album').success(function (data) {
        $scope.albums = data;
    });
    $scope.deleteAlbum = function (album) {
        new Album(album).$delete({id: album.id}, function () {
            $route.reload();
        });
    }
});

module.factory('Album', ['$resource', function ($resource) {
    return $resource(apiUrl + '/album/:id', {id: '@id'}, {
            shares: {url: apiUrl + '/album/shares', method: 'GET', isArray: true}
        });
}]);

module.factory('Profile', ['$resource', function ($resource) {
    return $resource(apiUrl + '/profile');
}]);

module.factory('AdminAlbum', ['$resource', function ($resource) {
    return $resource(apiUrl + '/admin/album/:id');
}]);

module.factory('authInterceptor', function ($q, $injector, $timeout, Identity) {
    return {
        request: function (request) {
            document.getElementById("output").innerHTML = '';
            request.headers.Authorization = 'Bearer ' + Identity.authc.token;
            return request;
        },
        responseError: function (rejection) {
            var status = rejection.status;

            if (status == 403 || status == 401) {
                document.getElementById("output").innerHTML = 'You can not access or perform the requested operation on this resource.';
            }

            return $q.reject(rejection);
        }
    };
});