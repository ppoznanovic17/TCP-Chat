package auth;

public enum LoginState {
	
	//uspeh
	LOGIN_ACCPETED,
	
	//sifra
	PASSWORD_MISMATCH,
	
	//registorvan uspesno
	REGISTERED,
	
	//greska
	INVALID_USERNAME,
	
	//vec se koristi
	IN_USE
}
