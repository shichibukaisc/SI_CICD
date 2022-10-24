package ca.alberta.services.sithdfca.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Slf4j
public class UrlSigner {
	
	// Note: Generally, you should store your private key someplace safe
	// and read them into your code

	// private static String keyString =
	// "f88af9b0dd65189104b3cc416616572af8cb27b7";//TDDefaults.getHashKey();

	public static String getSignature(String inputUrl, String hashCode) throws IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {
		return getSignature(inputUrl, hashCode, false);
	}

	public static String getSignature(String inputUrl, String hashCode, boolean queryParametersOnly)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {

		if (queryParametersOnly)
			return getSignatureQueryParametersOnly(inputUrl, hashCode);
		else
			return getSignatureFullUrl(inputUrl, hashCode);
	}

	private static String getSignatureQueryParametersOnly(String queryParameters, String hashCode)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {

		// initialize the UrlSigner with the cryptoKey
		UrlSigner signer = new UrlSigner(hashCode);
		/**
		 * %2F-/ %2E-. %3A-@
		 */
		// String fixedQP = queryParameters
		// .replace("%2F","/")
		// .replace("%2E",".")
		// .replace("%3A",":")
		// .replace("%40","@")
		// .replace(" ", "+");

		// String signedQueryParameters = signer.signRequest("", fixedQP, hashCode);
		String signedQueryParameters = signer.signRequest("", queryParameters, hashCode);
		return signedQueryParameters;
	}

	private static String getSignatureFullUrl(String inputUrl, String hashCode)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {

		// get the url to be encoded
		URL url = new URL(inputUrl);
		// initialize the UrlSigner with the cryptoKey
		UrlSigner signer = new UrlSigner(hashCode);
		String request = signer.signRequest(url.getPath(), url.getQuery(), hashCode);

		// System.out.println("Signed URL :" + url.getProtocol() + "://" + url.getHost()
		// + request);
		int port = url.getPort();
		String host = url.getHost();
		if (port > 0 && port != 80 && port != 443)
			host = host + ":" + port;
		return url.getProtocol() + "://" + host + request;
	}

	public UrlSigner(String keyString) throws IOException {
		keyString = keyString.replace('-', '+');
		keyString = keyString.replace('_', '/');
	}

	public String signRequest(String path, String query, String hashCode)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, URISyntaxException {
		return signRequest(path, query, hashCode, "SHA-1");
	}

	public String signRequest(String path, String query, String hashCode, String algorithm)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, URISyntaxException {

		// Retrieve the proper URL components to sign
		// String resource = query + hashCode;

		String hashValue = getHashForQueryString(query, hashCode);

		// MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		// byte[] result = mDigest.digest(resource.getBytes());
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < result.length; i++) {
		// sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		// }

		return path + "?" + query + "&hashValue=" + hashValue;
	}

	public static String getHashForQueryString(String qs, String hashCode)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, URISyntaxException {

		String resource = qs + hashCode;

		MessageDigest mDigest = MessageDigest.getInstance("SHA-1");
		byte[] result = mDigest.digest(resource.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static boolean validateHashedQuery(String query, String hashCode) {
		boolean isValid = false;
		String hashValue = "";
		String queryNoHash = "";
		String verifiedHash = "";
		int hashValueIdx = query != null ? query.lastIndexOf("hashValue") : -1;
		if (hashValueIdx > 0) {
			try {
				queryNoHash = query.substring(0, query.lastIndexOf("hashValue") - 1);
				verifiedHash = query.substring(hashValueIdx, query.length()).split("=")[1];
				hashValue = getHashForQueryString(queryNoHash, hashCode);
				System.err.println(String.format("Old: %s\nNew: %s", verifiedHash, hashValue));
				if (hashValue.equalsIgnoreCase(verifiedHash))
					isValid = true;
			} catch (Exception e) {
			}
		}

		// if the uri has a scheme and a domain, strip it. We only want to verify the
		// path

		return isValid;
	}

	public static String reEncodeQueryString(String queryString, List<String> excluded) {
		String reencoded = "";
		StringBuffer sb = new StringBuffer();
		for (String pairs : queryString.split("&")) {
			String[] values = pairs.split("=");
			if (values.length == 2) {
				// We have a key and a value (not just an empty key)
				String t = values[1];
				
				if (excluded == null || !excluded.contains(values[0])) {
					try {
						t = URLDecoder.decode(t, "UTF-8");
						t = URLEncoder.encode(t, "UTF-8");
					} catch (UnsupportedEncodingException ex) {
						t = "unsupportedEncodingException";
					}
					t = t.replace(".", "%2E") // Encode the period (.)
							.replace("-", "%2D") // Encode the dash (-)
							.replace("_", "%5F") // Encode the
							.replace("*", "%2A") // Encode the astrix (*)
					;
				}
				sb.append(values[0]);
				sb.append("=");
				sb.append(t);
				sb.append("&");
			} else {
				// we have an empty pair
				sb.append(values[0]);
				sb.append("=");
				sb.append("&");
			}
		}
		// get rid of the trailing &
		sb.deleteCharAt(sb.length() - 1);

		reencoded = sb.toString();
		// encode more characters such as - _ .
		log.debug(String.format("\nOriginal :\t%s\nReencoded :\t%s", queryString, reencoded));

		return reencoded;
	}
	
	public static void main(String[] args) {
//		String verifyUrl = "https://localhost:8443/epay/v1/verify-transaction?trnApproved=1&trnId=10000019&messageId=1&messageText=Approved&authCode=TEST&responseType=T&trnAmount=10.00&trnDate=3%2F11%2F2021+10%3A48%3A58+AM&trnOrderNumber=1&trnLanguage=eng&trnCustomerName=John+Test&trnEmailAddress=jj%2Eescott%40gov%2Eab%2Eca&trnPhoneNumber=7805555551&avsProcessed=1&avsId=N&avsResult=0&avsAddrMatch=0&avsPostalMatch=0&avsMessage=Street+address+and+Postal%2FZIP+do+not+match%2E&cvdId=1&cardType=VI&trnType=P&paymentMethod=CC&ref1=ref1_a&ref2=ref2_b&ref3=ref3_c&ref4=ref4_d&ref5=ref5_e&hashValue=6da9fb27a94370cb6085b686639b61fc4e39308f";
//		String verifyUrlNoHash = "https://localhost:8443/epay/v1/verify-transaction?trnApproved=1&trnId=10000019&messageId=1&messageText=Approved&authCode=TEST&responseType=T&trnAmount=10.00&trnDate=3%2F11%2F2021+10%3A48%3A58+AM&trnOrderNumber=1&trnLanguage=eng&trnCustomerName=John+Test&trnEmailAddress=jj%2Eescott%40gov%2Eab%2Eca&trnPhoneNumber=7805555551&avsProcessed=1&avsId=N&avsResult=0&avsAddrMatch=0&avsPostalMatch=0&avsMessage=Street+address+and+Postal%2FZIP+do+not+match%2E&cvdId=1&cardType=VI&trnType=P&paymentMethod=CC&ref1=ref1_a&ref2=ref2_b&ref3=ref3_c&ref4=ref4_d&ref5=ref5_e";
//		String decodedNoHash = "trnApproved=1&trnId=10000027&messageId=1&messageText=Approved&authCode=TEST&responseType=T&trnAmount=10.00&trnDate=3%2F16%2F2021+1%3A13%3A49+PM&trnOrderNumber=5&trnLanguage=eng&trnCustomerName=JJ+Test&trnEmailAddress=jj%2Eescott%40gov%2Eab%2Eca&trnPhoneNumber=7805555551&avsProcessed=1&avsId=N&avsResult=0&avsAddrMatch=0&avsPostalMatch=0&avsMessage=Street+address+and+Postal%2FZIP+do+not+match%2E&cvdId=1&cardType=VI&trnType=P&paymentMethod=CC&ref1=ref1%5Fa&ref2=ref2%5Fb&ref3=ref3%5Fc&ref4=ref4%5Fd&ref5=ref5%5Fe";
//		String newHash = "&hashValue=e733a83b12d1dbafcf07f261f5cfab716541e5f7";
//		String path = "";
		String hashCode = "test1234";
//		String hashValue = "";
		String query = "trnApproved=1&trnId=10000027&messageId=1&messageText=Approved&authCode=TEST&responseType=T&trnAmount=10.00&trnDate=3%2F16%2F2021+1%3A13%3A49+PM&trnOrderNumber=5&trnLanguage=eng&trnCustomerName=JJ+Test&trnEmailAddress=jj%2Eescott%40gov%2Eab%2Eca&trnPhoneNumber=7805555551&avsProcessed=1&avsId=N&avsResult=0&avsAddrMatch=0&avsPostalMatch=0&avsMessage=Street+address+and+Postal%2FZIP+do+not+match%2E&cvdId=1&cardType=VI&trnType=P&paymentMethod=CC&ref1=ref1%5Fa&ref2=ref2%5Fb&ref3=ref3%5Fc&ref4=ref4%5Fd&ref5=ref5%5Fe&hashValue=e733a83b12d1dbafcf07f261f5cfab716541e5f7";
//		String queryNoHash = "";
//		String signedUrl = "";

//		URL url = null;
		try {
			boolean isValid = UrlSigner.validateHashedQuery(query, hashCode);
			String msg = isValid ? "Its good!" : "Its not good :(";
			
			System.err.println(msg);
			// query = verifyUrl;

			// return the query string without the hashValue
			// int idx = query.lastIndexOf("hashValue");
			// queryNoHash = query.substring(0,idx-1);
			// hashValue = query.substring(idx, query.length()).split("=")[1];

			// We now have the hashValue and the query without the hashValue. Lets hash the
			// query and then compare the hashes
			// int port = url.getPort();
			// String host = url.getHost();
			// if(port != 80 && port != 443)
			// host = host + ":" + port;
			// signedUrl =
			// UrlSigner.getSignature(String.format("%s://%s%s?%s",url.getProtocol(), host,
			// path, queryNoHash), hashCode);
			//
			// if(signedUrl.contains(hashValue))
			// System.out.println("Verified!!");
			// else
			// System.err.println("Not Verified!!");

		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
		}

	}

}