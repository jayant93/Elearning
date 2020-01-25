/**
 * Created by jpereira on 12/19/2016.
 */
module.exports = {
    loginController: function ($scope, ENV_VARS, userService, recaptchaService, vcRecaptchaService, localStorageService) {
        $scope.recaptchaKey = ENV_VARS.recaptchaPublic;

         $scope.login = function () {
            if($scope.recaptchaKey){
                recaptchaService.verify(vcRecaptchaService.getResponse()).then(function (data) {
                    $scope.sumbitLoginForm(); 
                }, function (reason) {
                    vcRecaptchaService.reload();
                });
            }else{
                $scope.sumbitLoginForm();
            }
        }

        $scope.sumbitLoginForm = function(){
			
			if ($scope.loginAllowed()) {	
					userService.login($scope.username, $scope.password).then(function (url) {
					//Go to view document page
					window.location.href = url;
				}, function (reason) {
					$scope.errorMessage = reason.message;
					vcRecaptchaService.reload();
					$scope.increaseLoginAttempt();
				});
			
			}else {
				$scope.errorMessage = 'Login is locked, please try again later.';
			}
			
        }
		
		$scope.increaseLoginAttempt = function(){
			
			var loginAttempt = localStorageService.get('login-attempt');
			
			if (loginAttempt) {
				loginAttempt = parseInt(loginAttempt) + 1;
				
				if(loginAttempt === 3){
						$scope.lockLogin();
				}
				
			} else {
				loginAttempt = 1;
			}
			
			localStorageService.set('login-attempt', loginAttempt);
		}
		
		
		$scope.resetLoginAttempt = function() {
			localStorageService.set('login-attempt', 0);
			localStorageService.set('login-lock', null);
		}
		
		$scope.lockLogin = function () {
			var expirationMin = 5;
			var expirationMS = expirationMin * 60 * 1000;
			var lockedEnd = new Date().getTime() + expirationMS;
			
			localStorageService.set('login-lock', JSON.stringify(lockedEnd));
		}
		
		$scope.loginAllowed = function () {
			
			var result = true;
			var lock = localStorageService.get('login-lock');
			
			if (lock) {
				
				var lockTime = JSON.parse(lock);
				if ((new Date().getTime()) <= lockTime) {
					result = false;
				} else {
					// unlock
					$scope.resetLoginAttempt();
				}
			}
			
			return result;
				
		}
		
		
		
    }
}