package app

import grails.gorm.DetachedCriteria
import grails.compiler.GrailsCompileStatic
import groovy.transform.ToString
import org.codehaus.groovy.util.HashCodeHelper

@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class AppUserRole implements Serializable {

	private static final long serialVersionUID = 1

	AppUser appUser
	Role role

	@Override
	boolean equals(other) {
		if (other instanceof AppUserRole) {
			other.appUserId == appUser?.id && other.roleId == role?.id
		}
	}

	@Override
	int hashCode() {
		int hashCode = HashCodeHelper.initHash()
		if (appUser) {
			hashCode = HashCodeHelper.updateHash(hashCode, appUser.id)
		}
		if (role) {
			hashCode = HashCodeHelper.updateHash(hashCode, role.id)
		}
		hashCode
	}

	static AppUserRole get(UUID appUserId, UUID roleId) {
		criteriaFor(appUserId, roleId).get()
	}

	static boolean exists(UUID appUserId, UUID roleId) {
		criteriaFor(appUserId, roleId).count()
	}

	private static DetachedCriteria<AppUserRole> criteriaFor(UUID appUserId, UUID roleId) {
		AppUserRole.where {
			appUser == AppUser.load(appUserId) &&
					role == Role.load(roleId)
		}
	}

	static AppUserRole create(AppUser appUser, Role role, boolean flush = false) {
		def instance = new AppUserRole(appUser: appUser, role: role)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(AppUser u, Role r) {
		if (u != null && r != null) {
			AppUserRole.where { appUser == u && role == r }.deleteAll()
		}
	}

	static int removeAll(AppUser u) {
		u == null ? 0 : AppUserRole.where { appUser == u }.deleteAll() as int
	}

	static int removeAll(Role r) {
		r == null ? 0 : AppUserRole.where { role == r }.deleteAll() as int
	}

	static constraints = {
		appUser nullable: false
		role nullable: false, validator: { Role r, AppUserRole ur ->
			if (ur.appUser?.id) {
				if (AppUserRole.exists(ur.appUser.id as UUID, r.id as UUID)) {
					return ['userRole.exists']
				}
			}
		}
	}

	static mapping = {
		id      composite: ['appUser', 'role']
		version false
	}
}