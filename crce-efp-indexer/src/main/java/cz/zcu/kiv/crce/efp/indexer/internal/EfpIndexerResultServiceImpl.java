package cz.zcu.kiv.crce.efp.indexer.internal;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;

import cz.zcu.kiv.crce.efp.indexer.EfpIndexerResultService;

/**
 * Implementation of EfpIndexerResultService interface.
 */
public class EfpIndexerResultServiceImpl implements EfpIndexerResultService {

	/** This instance holds information about result of indexing process. */
	private String message = null;

	/** Constructor of EfpIndexerResultServiceImpl class. */
	public EfpIndexerResultServiceImpl() {
		message = "EfpIndexerLogService was initialized.";
	}

	/**
	 * Getting information.
	 *
	 * @return Information about indexing result for user.
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * Setting information.
	 *
	 * @param message - Information about indexing result.
	 */
	public final void setMessage(final String message) {
		this.message = message;
	}

	@Override
	public final void updated(final Dictionary properties) throws ConfigurationException {
		return;
	}
}
