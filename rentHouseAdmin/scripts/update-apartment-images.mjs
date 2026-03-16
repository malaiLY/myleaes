import fs from 'node:fs/promises'
import path from 'node:path'

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080'
const TOKEN = process.env.ACCESS_TOKEN || ''
const APARTMENT_NAME = process.env.APARTMENT_NAME || '温都水城社区'
const IMAGE_DIR =
  process.env.IMAGE_DIR || 'C:\\Users\\33\\Desktop\\毕业生\\软件用图\\公寓外观'
const PAGE_SIZE = Number(process.env.PAGE_SIZE || 200)
const APPEND_MODE = process.env.APPEND_MODE === 'true'

if (!TOKEN) {
  console.error('缺少 ACCESS_TOKEN，无法调用后台接口。')
  process.exit(1)
}

function buildUrl(urlPath, query = {}) {
  const url = new URL(urlPath, BASE_URL)
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.set(key, String(value))
    }
  })
  return url
}

async function apiGet(urlPath, query = {}) {
  const response = await fetch(buildUrl(urlPath, query), {
    headers: {
      'access-token': TOKEN,
    },
  })
  const data = await response.json()
  if (!response.ok || data.code !== 200) {
    throw new Error(`GET ${urlPath} 失败: ${data.message || response.statusText}`)
  }
  return data.data
}

async function apiPostJson(urlPath, body) {
  const response = await fetch(buildUrl(urlPath), {
    method: 'POST',
    headers: {
      'access-token': TOKEN,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  })
  const data = await response.json()
  if (!response.ok || data.code !== 200) {
    throw new Error(`POST ${urlPath} 失败: ${data.message || response.statusText}`)
  }
  return data.data
}

async function uploadImage(filePath) {
  const formData = new FormData()
  const fileBuffer = await fs.readFile(filePath)
  const fileName = path.basename(filePath)
  formData.append('file', new Blob([fileBuffer]), fileName)

  const response = await fetch(buildUrl('/admin/file/upload'), {
    method: 'POST',
    headers: {
      'access-token': TOKEN,
    },
    body: formData,
  })
  const data = await response.json()
  if (!response.ok || data.code !== 200) {
    throw new Error(`上传 ${fileName} 失败: ${data.message || response.statusText}`)
  }
  return {
    name: fileName,
    url: data.data,
  }
}

async function fetchAllPages(urlPath, query = {}) {
  const firstPage = await apiGet(urlPath, {
    current: 1,
    size: PAGE_SIZE,
    ...query,
  })
  const firstRecords = Array.isArray(firstPage.records) ? firstPage.records : []
  const total = Number(firstPage.total || firstRecords.length)
  const totalPages = total > 0 ? Math.ceil(total / PAGE_SIZE) : 1

  if (totalPages === 1) return firstRecords

  const rest = []
  for (let page = 2; page <= totalPages; page += 1) {
    const current = await apiGet(urlPath, {
      current: page,
      size: PAGE_SIZE,
      ...query,
    })
    if (Array.isArray(current.records)) {
      rest.push(...current.records)
    }
  }
  return firstRecords.concat(rest)
}

async function getApartmentByName(name) {
  const apartments = await fetchAllPages('/admin/apartment/pageItem')
  return apartments.find((item) => item.name === name)
}

async function getApartmentDetail(id) {
  return apiGet('/admin/apartment/getDetailById', { id })
}

function pickRandom(list) {
  return list[Math.floor(Math.random() * list.length)]
}

async function getImageFiles() {
  const entries = await fs.readdir(IMAGE_DIR, { withFileTypes: true })
  const files = entries
    .filter((entry) => entry.isFile())
    .map((entry) => path.join(IMAGE_DIR, entry.name))
    .filter((filePath) => /\.(png|jpe?g|webp)$/i.test(filePath))

  const exteriorImages = files.filter((filePath) =>
    path.basename(filePath).startsWith('公寓外观'),
  )
  const gymImage = files.find((filePath) => path.basename(filePath).startsWith('健身房'))

  if (!exteriorImages.length || !gymImage) {
    throw new Error('图片目录中没有找到“公寓外观”或“健身房”图片。')
  }

  return {
    exteriorFile: pickRandom(exteriorImages),
    gymFile: gymImage,
  }
}

function buildSavePayload(detail, uploadedImages) {
  const existingImages = Array.isArray(detail.graphVoList) ? detail.graphVoList : []

  return {
    id: detail.id,
    name: detail.name,
    introduction: detail.introduction,
    districtId: detail.districtId,
    districtName: detail.districtName,
    cityId: detail.cityId,
    cityName: detail.cityName,
    provinceId: detail.provinceId,
    provinceName: detail.provinceName,
    addressDetail: detail.addressDetail,
    latitude: detail.latitude,
    longitude: detail.longitude,
    phone: detail.phone,
    isRelease: detail.isRelease,
    facilityInfoIds: detail.facilityInfoList?.map((item) => item.id) || [],
    labelIds: detail.labelInfoList?.map((item) => item.id) || [],
    feeValueIds: detail.feeValueVoList?.map((item) => item.id) || [],
    graphVoList: APPEND_MODE ? existingImages.concat(uploadedImages) : uploadedImages,
  }
}

async function main() {
  console.log(`开始处理公寓：${APARTMENT_NAME}`)
  const apartment = await getApartmentByName(APARTMENT_NAME)
  if (!apartment) {
    throw new Error(`没有找到公寓：${APARTMENT_NAME}`)
  }

  const detail = await getApartmentDetail(apartment.id)
  const { exteriorFile, gymFile } = await getImageFiles()
  console.log(`选择图片：${path.basename(exteriorFile)} + ${path.basename(gymFile)}`)

  const [exteriorUpload, gymUpload] = await Promise.all([
    uploadImage(exteriorFile),
    uploadImage(gymFile),
  ])

  const payload = buildSavePayload(detail, [exteriorUpload, gymUpload])
  await apiPostJson('/admin/apartment/saveOrUpdate', payload)
  console.log('公寓图片上传完成。')
}

main().catch((error) => {
  console.error(error.message || error)
  process.exit(1)
})
