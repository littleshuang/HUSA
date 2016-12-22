package NLP;

/**
 * Created by Ziyun on 2016/12/15.
 */

public class NlpTools {

	/**
	 * 将中科院词性标注转换为哈工大词性标注
	 *
	 * @param originTag 中科院词性标注
	 * @return 哈工大词性标注
	 */
	public static String translateTag(String originTag) {
		String result;
		char begin = originTag.charAt(0);
		switch (begin) {
			case 'a':
				switch (originTag) {
					case "ag":
						result = "ga";
						break;
					case "al":
						result = "ia";
						break;
					default:
						result = "a";
				}
				break;
			case 'b':
				result = "f";
				break;
			case 'c':
				result = "c";
				break;
			case 'f':
				result = "nd";
				break;
			case 'm':
				result = "m";
				break;
			case 'n':
				if (originTag.length() >= 2) {
					switch (originTag.charAt(1)) {
						case 'c':
							result = "nc";
							break;
						case 'r':
							result = "nh";
							break;
						case 's':
							result = "ns";
							break;
						case 't':
							result = "ni";
							break;
						case 'z':
							result = "nz";
							break;
						case 'l':
							result = "nz";
							break;
						// case 'g':
						// 	result = "gn";
						// 	break;
						default:
							result = "n";
					}
				} else {
					result = "n";
				}
				break;
			case 'p':
				result = "p";
				break;
			case 'q':
				result = "q";
				break;
			case 'r':
				result = "r";
				break;
			case 's':
				result = "nl";
				break;
			case 't':
				switch (originTag) {
					case "tg":
						result = "gn";
						break;
					default:
						result = "nt";
				}
				break;
			case 'u':
				result = "u";
				break;
			case 'v':
				result = "v";
				// switch (originTag) {
				// 	case "vshi":
				// 		result = "vt";
				// 		break;
				// 	case "vyou":
				// 		result = "vt";
				// 		break;
				// 	case "vf":
				// 		result = "vd";
				// 		break;
				// 	case "vi":
				// 		result = "vi";
				// 		break;
				// 	case "vl":
				// 		result = "iv";
				// 		break;
				// 	case "vg":
				// 		result = "gv";
				// 		break;
				// 	default:
				// 		result = "v";
				// }
				break;
			case 'w':
				result = "wp";
				break;
			case 'x':
				result = "ws";
				break;
			case 'y':
				result = "x";
				break;
			case 'z':
				result = "d";
				break;
			default:
				result = originTag;
		}
		return result;
	}
}
