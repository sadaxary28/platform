package com.infomaximum.platform.sdk.component;

import com.infomaximum.subsystems.utils.Version;

public class Info extends com.infomaximum.cluster.struct.Info {

	private final Version version;

	protected Info(Info.Builder builder) {
		super(builder);
		this.version = builder.version;
	}

	public Version getVersion() {
		return version;
	}

	public static class Builder<T extends Builder> extends com.infomaximum.cluster.struct.Info.Builder<T> {

		private final Version version;

		public Builder(String uuid, Version version) {
			super(uuid);
			this.version = version;
		}

		@Override
		public Info build() {
			return new Info(this);
		}

	}
}
