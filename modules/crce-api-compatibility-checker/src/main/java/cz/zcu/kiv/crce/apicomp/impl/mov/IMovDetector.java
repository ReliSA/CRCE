package cz.zcu.kiv.crce.apicomp.impl.mov;

/**
 * Base interface for all MOV detectors. The actual logic for detecting MOV
 * may differ from one API type (e.g. WADL) to another (e.g. WSDL).
 */
public interface IMovDetector {


    MovDetectionResult detectMov();
}
