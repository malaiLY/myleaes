import fs from 'node:fs/promises'
import path from 'node:path'

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080'
const TOKEN = process.env.ACCESS_TOKEN || ''
const APARTMENT_NAME =
  process.env.APARTMENT_NAME || '远望公寓·未来科学城'
const IMAGE_DIR =
  process.env.IMAGE_DIR || 'C:\\Users\\33\\Desktop\\毕业生\\软件用图\\卧室'
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
  formData.append(
    'file',
    new Blob([fileBuffer]),
    fileName,
  )

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
    fileName,
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

async function getRoomsByApartmentId(apartmentId, apartmentName) {
  const rooms = await fetchAllPages('/admin/room/pageItem')
  return rooms.filter(
    (item) =>
      Number(item.apartmentId) === Number(apartmentId) ||
      item.apartmentInfo?.name === apartmentName,
  )
}

async function getRoomDetail(id) {
  return apiGet('/admin/room/getDetailById', { id })
}

function pickRandom(list) {
  return list[Math.floor(Math.random() * list.length)]
}

async function getImagePools() {
  const entries = await fs.readdir(IMAGE_DIR, { withFileTypes: true })
  const files = entries
    .filter((entry) => entry.isFile())
    .map((entry) => path.join(IMAGE_DIR, entry.name))
    .filter((filePath) => /\.(png|jpe?g|webp)$/i.test(filePath))

  const bedroomImages = files.filter((filePath) =>
    path.basename(filePath).startsWith('卧室'),
  )
  const livingRoomImages = files.filter((filePath) =>
    path.basename(filePath).startsWith('客厅'),
  )

  if (!bedroomImages.length || !livingRoomImages.length) {
    throw new Error('图片目录中没有找到“卧室*”或“客厅*”图片。')
  }

  return { bedroomImages, livingRoomImages }
}

function buildSavePayload(detail, uploadedImages) {
  const existingImages = Array.isArray(detail.graphVoList)
    ? detail.graphVoList.map((item, index) => ({
        name: Number(item.name || index + 1),
        url: item.url,
      }))
    : []

  const newImages = uploadedImages.map((item, index) => ({
    name: existingImages.length + index + 1,
    url: item.url,
  }))

  return {
    id: detail.id,
    roomNumber: String(detail.roomNumber),
    rent: detail.rent,
    apartmentId: detail.apartmentId,
    isRelease: detail.isRelease,
    attrValueIds: detail.attrValueVoList?.map((item) => item.id) || [],
    facilityInfoIds: detail.facilityInfoList?.map((item) => item.id) || [],
    labelInfoIds: detail.labelInfoList?.map((item) => item.id) || [],
    paymentTypeIds: detail.paymentTypeList?.map((item) => item.id) || [],
    leaseTermIds: detail.leaseTermList?.map((item) => item.id) || [],
    graphVoList: APPEND_MODE ? existingImages.concat(newImages) : newImages,
  }
}

async function main() {
  console.log(`开始处理公寓：${APARTMENT_NAME}`)
  const apartment = await getApartmentByName(APARTMENT_NAME)
  if (!apartment) {
    throw new Error(`没有找到公寓：${APARTMENT_NAME}`)
  }

  const rooms = await getRoomsByApartmentId(apartment.id, apartment.name)
  if (!rooms.length) {
    console.log('该公寓下没有房间，无需处理。')
    return
  }

  const { bedroomImages, livingRoomImages } = await getImagePools()
  console.log(`找到 ${rooms.length} 个房间，开始上传图片...`)

  for (const room of rooms) {
    const bedroomFile = pickRandom(bedroomImages)
    const livingRoomFile = pickRandom(livingRoomImages)

    console.log(`处理房间 ${room.roomNumber}：${path.basename(bedroomFile)} + ${path.basename(livingRoomFile)}`)

    const [bedroomUpload, livingRoomUpload, detail] = await Promise.all([
      uploadImage(bedroomFile),
      uploadImage(livingRoomFile),
      getRoomDetail(room.id),
    ])

    const payload = buildSavePayload(detail, [bedroomUpload, livingRoomUpload])
    await apiPostJson('/admin/room/saveOrUpdate', payload)
  }

  console.log('全部房间图片上传完成。')
}

main().catch((error) => {
  console.error(error.message || error)
  process.exit(1)
})
