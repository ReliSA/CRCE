package cz.zcu.kiv.crce.plugin.internal;

import java.util.Dictionary;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import cz.zcu.kiv.crce.plugin.MetadataIndexingResultService;


/**
 * Implementation of MetadataIndexingResultService interface.
 */
public class MetadataIndexingResultServiceImpl implements MetadataIndexingResultService, ManagedService {

	/** This instance holds information about result of indexing process. */
	private String message = null;

	private boolean isEmpty;

	/** Constructor of MetadataIndexingResultServiceImpl class. */
	public MetadataIndexingResultServiceImpl() {
		message = "";
		isEmpty = true;
	}

	/**
	 * Getting information.
	 *
	 * @return Information about indexing result for user.
	 */
	@Override
	public final String getMessage() {
		if(isEmpty){
			return "";
		}
		else{
			return message;
		}
	}

	/**
	 * Setting information.
	 *
	 * @param message - Information about indexing result.
	 */
	@Override
	public final boolean setMessage(final String message) {
		if(isEmpty){
			this.message = message;
			isEmpty = false;
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public final void addMessage(final String nextMessage) {
		this.message+=" "+nextMessage;
		isEmpty = false;
	}

	/**
	 * @return the isEmpty
	 */
	@Override
	public boolean isEmptyMessageString() {
		return isEmpty;
	}

	@Override
	public void resetMessageString() {
		message = "";
		isEmpty = true;
	}

	@Override
	public final void updated(final Dictionary properties) throws ConfigurationException {
		return;
	}
}
