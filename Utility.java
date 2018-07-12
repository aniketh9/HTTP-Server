
public class Utility {

	// getContentType method checks the ".extension" of a given file and returns
	// appropriate content type .

	public static String getContentType(String fileName) {

		String extension = "";
		String fileType = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}

		switch (extension) {
		case "htm":
		case "html":
			fileType = "text/html";
			break;
		case "jpg":
		case "jpeg":
			fileType = "image/jpeg";
			break;
		case "gif":
			fileType = "image/gif";
			break;
		case "pdf":
			fileType = "application/pdf";
			break;
		case "deb":
		case "udeb":
			fileType = "application/x-debian-package";
			break;
		case "tif":
		case "tiff":
			fileType = "image/tiff";
			break;
		default:
			fileType = "application/octet-stream";
		}
		return fileType;
	}

}
