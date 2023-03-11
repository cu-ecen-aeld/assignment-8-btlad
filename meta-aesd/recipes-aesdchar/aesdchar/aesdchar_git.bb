#  See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Path to the assignments rep.  Use ssh protocol for passwordless access.
SRC_URI = "git://git@github.com/cu-ecen-aeld/assignments-3-and-later-btlad.git;protocol=ssh;branch=main \
           file://aesdchar.init \
          "

PV = "1.0+git${SRCPV}"

# Set to reference a specific commit hash in a assignment repo.
SRCREV = "66973692714b01a694c657e09e6ccd3d8a7a235c"

# This sets a staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# Reference the "aesd-char-driver" directory here to build from the "aesd-char-driver" directory
# in the assignments repo
S = "${WORKDIR}/git/aesd-char-driver"

inherit module

EXTRA_OEMAKE:append:task-install = " -C ${STAGING_KERNEL_DIR} M=${S}"
EXTRA_OEMAKE += "KERNELDIR=${STAGING_KERNEL_DIR}"

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

RPROVIDES:${PN} += "kernel-module-aesdchar"

# Add the aesdchar init files need to install
FILES:${PN} += "${sysconfdir}/init.d/aesdchar.init"

inherit update-rc.d

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME:${PN} = "aesdchar.init"

do_install () {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake DEPMOD=echo MODLIB="${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}" \
               INSTALL_FW_PATH="${D}${nonarch_base_libdir}/firmware" \
               CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
               O=${STAGING_KERNEL_BUILDDIR} \
               ${MODULES_INSTALL_TARGET}

    if [ ! -e "${B}/${MODULES_MODULE_SYMVERS_LOCATION}/Module.symvers" ] ; then
        bbwarn "Module.symvers not found in ${B}/${MODULES_MODULE_SYMVERS_LOCATION}"
        bbwarn "Please consider setting MODULES_MODULE_SYMVERS_LOCATION to a"
        bbwarn "directory below B to get correct inter-module dependencies"
    else
        install -Dm0644 "${B}/${MODULES_MODULE_SYMVERS_LOCATION}"/Module.symvers ${D}${includedir}/${BPN}/Module.symvers
        # Module.symvers contains absolute path to the build directory.
        # While it doesn't actually seem to matter which path is specified,
        # clear them out to avoid confusion
        sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
    fi

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/../../aesdchar.init ${D}${sysconfdir}/init.d
}
