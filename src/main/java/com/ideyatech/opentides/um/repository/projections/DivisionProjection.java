package com.ideyatech.opentides.um.repository.projections;

import com.ideyatech.opentides.um.entity.Division;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author jpereira on 3/23/2017.
 */
@Projection(types = Division.class)
public interface DivisionProjection {

    Long getId();

    String getNosqlId();

    String getKey();

    String getName();

    String getDescription();

    Division getDivision();
}
